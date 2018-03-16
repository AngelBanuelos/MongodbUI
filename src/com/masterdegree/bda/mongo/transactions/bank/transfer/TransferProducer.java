/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.transfer;

import com.masterdegree.bda.mongo.db.sensors.utils.Utils;
import com.masterdegree.bda.mongo.transactions.bank.account.AccountHelper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel_banuelos
 */
public class TransferProducer implements Runnable {

    AccountHelper accountHelper;
    TransferList list;
    private long transferID;

    public TransferProducer(AccountHelper accountHelper, TransferList list) {
        this.accountHelper = accountHelper;
        this.list = list;
        transferID = accountHelper.getNextTansferID();
    }

    @Override
    public void run() {
        /// testing
        int x = 0;
        while (x < 100) {
            createTransfer("A", "B", (float) Utils.rndDouble(0, 1001));
            createTransfer("B", "C", (float) Utils.rndDouble(0, 1001));
            createTransfer("C", "A", (float) Utils.rndDouble(0, 1001));
            createTransfer("A", "C", (float) Utils.rndDouble(0, 1001));
            createTransfer("C", "B", (float) Utils.rndDouble(0, 1001));
            createTransfer("B", "A", (float) Utils.rndDouble(0, 1001));
            try {
                Thread.sleep(500);
                x++;
            } catch (InterruptedException ex) {
                Logger.getLogger(TransferProducer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //loadPendingTransactions();
    }

    public void createTransfer(String source, String destination, float amount) {
        if (accountHelper.exists(source) && accountHelper.exists(destination)) {
            if (accountHelper.hasSufficientFunds(source, amount)) {
                list.push(new Transfer(++transferID, source, destination, amount, accountHelper.getMongoCollection("Transactions")));
//                new Transfer(++transferID, source, destination, amount, accountHelper.getMongoCollection("Transactions"));
            } else {
                // no sufficient funds exception.
                System.err.println("No sufficient funds");
            }
        } else {
            // some accounts do not exists.
            System.err.println("account do not exist");
        }
    }
}
