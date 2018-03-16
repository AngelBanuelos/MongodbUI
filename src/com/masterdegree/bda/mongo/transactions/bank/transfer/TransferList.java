/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.transfer;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author angel_banuelos
 */
public class TransferList {

    private final Queue<Transfer> transferList = new ArrayDeque<Transfer>();
    private final Set<Transfer> uniques = new HashSet();

    public TransferList() {
        Collections.synchronizedSet(uniques);
    }

    public synchronized void push(Transfer t) {
        if (uniques.add(t)) {
            transferList.offer(t);
            this.notify();
        } else {
            System.out.println("Error: " + t.getId() + " Already in the collection ");
        }
    }

    public synchronized Transfer pop() throws InterruptedException {
        if (transferList.isEmpty()) {
            this.wait(1000 * 10);
        }
        if (!transferList.isEmpty()) {
            Transfer t = transferList.peek();
            if (t.getState() == Transfer.State.DONE
                    || t.getState() == Transfer.State.ERROR_EXPIRED
                    || t.getState() == Transfer.State.CANCELLED_NO_SUFFICIENT_FOUDS) {
                uniques.remove(t);
                transferList.remove(t);
            }
            return t;
        }
        return null;
    }

    public synchronized boolean isEmpty() {
        return transferList.isEmpty();
    }

    public synchronized void pendingTransactions(Queue<Transfer> pendingTransactions) {
        while (!pendingTransactions.isEmpty()) {
            push(pendingTransactions.poll());
        }
    }
}
