/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.main;

import com.masterdegree.bda.mongo.connector.MongoConnection;
import com.masterdegree.bda.mongo.transactions.bank.account.Account;
import com.masterdegree.bda.mongo.transactions.bank.account.AccountHelper;
import com.masterdegree.bda.mongo.transactions.bank.transfer.TransferConsumer;
import com.masterdegree.bda.mongo.transactions.bank.transfer.TransferList;
import com.masterdegree.bda.mongo.transactions.bank.transfer.TransferProducer;

/**
 *
 * @author angel_banuelos
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        MongoConnection con = MongoConnection.createInstance();
        TransferList list = new TransferList();
        AccountHelper accountHelper = new AccountHelper(con);

//        Account A = new Account(1, "A", 50000.00f, accountHelper.getMongoCollection("Account"));
//
//        Account B = new Account(2, "B", 1000.00f, accountHelper.getMongoCollection("Account"));

        // this will create 10 trannsfers
        TransferProducer t = new TransferProducer(accountHelper, list);
        new Thread(t).start();

        //Thread.sleep(2000);
        TransferConsumer c = new TransferConsumer(list, accountHelper);
        new Thread(c).start();
        new Thread(c).start();
        new Thread(c).start();

    }
}
