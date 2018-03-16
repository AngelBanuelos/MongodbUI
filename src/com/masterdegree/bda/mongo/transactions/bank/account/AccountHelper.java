/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.account;

import com.masterdegree.bda.mongo.connector.MongoConnection;
import com.masterdegree.bda.mongo.transactions.bank.transfer.Transfer;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.ArrayDeque;
import java.util.Queue;
import org.bson.Document;

/**
 *
 * @author angel_banuelos
 */
public class AccountHelper {

    private MongoConnection con;

    public AccountHelper(MongoConnection con) {
        this.con = con;
    }

    public synchronized boolean exists(String account) {
        FindIterable<Document> d = getMongoCollection("Account")
                .find(Filters.eq("name", account));
        for (Document document : d) {
            // if exists at lease one account return true
            return true;
        }
        return false;
    }

    public synchronized boolean hasSufficientFunds(String account, float amount) {
        FindIterable<Document> d = getMongoCollection("Account")
                .find(Filters.eq("name", account));
        for (Document document : d) {
            // if exists at lease one account return true
            Double balance = document.getDouble("balance");
            if (balance != null && balance >= amount) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public synchronized MongoConnection getConnection() {
        return con;
    }

    public synchronized MongoCollection getMongoCollection(String collection) {
        MongoCollection coll = con.connection().getDatabase("BANK").getCollection(collection);
        return coll;
    }

    public synchronized Account findAccount(String account) {
        FindIterable<Document> d = getMongoCollection("Account")
                .find(Filters.eq("name", account));
        Account accounT = null;
        for (Document document : d) {
            accounT = new Account(document, getMongoCollection("Account"));
        }
        return accounT;
    }

    public  synchronized long getNextTansferID() {
        return getMongoCollection("Transactions").count();
    }

    public synchronized Queue<Transfer> pendingTransactions() {
        FindIterable<Document> d = getMongoCollection("Transactions")
                .find(Filters.nin("state", "DONE", "CANCELLED_FUNDS", "ERROR_EXPIRED"));
        Queue<Transfer> l = new ArrayDeque<>();
        for (Document document : d) {
            l.offer(new Transfer(document, getMongoCollection("Transactions")));
        }
        return l;
    }

    public synchronized long getNextAccountID() {
        return getMongoCollection("Account").count();
    }
    
}
