/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.transfer;

import com.masterdegree.bda.mongo.db.sensors.utils.Utils;
import com.masterdegree.bda.mongo.transactions.bank.account.Account;
import com.masterdegree.bda.mongo.transactions.bank.account.AccountHelper;
import com.masterdegree.bda.mongo.transactions.bank.account.InvalidAmount;
import com.masterdegree.bda.mongo.transactions.bank.account.NoSuffictientFunds;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel_banuelos
 */
public class TransferConsumer implements Runnable {

    private TransferList list;
    private AccountHelper accountHelper;
    public static volatile boolean stop = false;

    public TransferConsumer(TransferList list, AccountHelper accountHelper) {
        this.list = list;
        this.accountHelper = accountHelper;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                if (list.isEmpty()) { 
                    Thread.sleep(1000 * 10);
                }
                Transfer t = list.pop();
                if (t == null) {
                    continue;
                }
                switch (t.getState()) {
                    case NEW:
                        processNewTranfers(t);
                        break;
                    case PENDING:
                        processPendingTranfers(t);
                        break;
                    case APPLIED:
                        processAppliedTranfers(t);
                        break;
                    case CANCELLING:
                        processCancellingTranfers(t);
                        break;
                    case CANCELLED_NO_SUFFICIENT_FOUDS:
                        processCancelledTranfers(t);
                        break;
                    case DONE:
                        processDoneTranfers(t);
                        break;
                    case ERROR:
                        processErrorTranfers(t);
                        break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TransferConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private synchronized void processNewTranfers(Transfer t) {
        t.setState(Transfer.State.PENDING);
        t.updateStateInDB(); // update transactions to Pending
//        list.push(t);
    }

    private synchronized void processPendingTranfers(Transfer t) {
        try {
            if (t.timeExpired()) {
                t.setState(Transfer.State.ERROR);
                t.updateStateInDB();
                return;
            }
            Account accountSource = accountHelper.findAccount(t.getSource());
            Account accountDest = accountHelper.findAccount(t.getDestination());
            if (accountSource == null || accountDest == null) {
                t.setState(Transfer.State.ERROR);
                t.updateStateInDB();
                return;
            }

            accountSource.addTransfer(t);
            accountDest.addTransfer(t);
            // update account withdraw money in source account
            accountSource.withdraw(t.getAmount());

            accountDest.deposit(t.getAmount());
            t.setState(Transfer.State.APPLIED);
            t.updateStateInDB();
        } catch (NoSuffictientFunds | InvalidAmount ex) {
            ex.printStackTrace();
            t.setState(Transfer.State.ERROR);
            t.updateStateInDB();
        }
//        list.push(t);
    }

    private synchronized void processAppliedTranfers(Transfer t) {
        Account accountSource = accountHelper.findAccount(t.getSource());
        accountSource.pullTransferIDInDB(t);

        Account accountDest = accountHelper.findAccount(t.getDestination());
        accountDest.pullTransferIDInDB(t);
        t.setState(Transfer.State.DONE);
        processDoneTranfers(t);
//        list.push(t);
    }

    private synchronized void processDoneTranfers(Transfer t) {
        // Cancel Transaction
        if (isLostConnection()) {
            t.setState(Transfer.State.CANCELLING);
            t.updateStateInDB();
            System.out.println("Lost Connection");
            list.push(t);
        } else {
            t.updateStateInDB();
        }
    }

    // future release
    private synchronized void processCancellingTranfers(Transfer t) {
        try {
            Account accountSource = accountHelper.findAccount(t.getSource());
            Account accountDest = accountHelper.findAccount(t.getDestination());
            if (accountSource == null || accountDest == null) {
                t.setState(Transfer.State.ERROR);
                t.updateStateInDB();
                return;
            }

            // update account withdraw money in source account
            accountDest.withdraw(t.getAmount());
            accountSource.deposit(t.getAmount());

            t.setState(Transfer.State.NEW);
            t.updateStateInDB();
        } catch (NoSuffictientFunds | InvalidAmount ex) {
            ex.printStackTrace();
            t.setState(Transfer.State.CANCELLED_NO_SUFFICIENT_FOUDS);
            t.updateStateInDB();
        }
//        list.push(t);
    }

    private synchronized void processCancelledTranfers(Transfer t) {
        Account accountSource = accountHelper.findAccount(t.getSource());
        Account accountDest = accountHelper.findAccount(t.getDestination());
        accountSource.pullTransferIDInDB(t);
        accountDest.pullTransferIDInDB(t);
        t.updateStateInDB();
    }

    private synchronized void processErrorTranfers(Transfer t) {
        if (!t.timeExpired()) {
            t.setState(Transfer.State.CANCELLED_NO_SUFFICIENT_FOUDS);
            t.updateStateInDB();
        } else {
            t.setState(Transfer.State.ERROR_EXPIRED);
            t.updateStateInDB();
        }
//        list.push(t);
    }

    private synchronized boolean isLostConnection() {
        return Utils.rndInt(0, 101) >= 90;
    }

}
