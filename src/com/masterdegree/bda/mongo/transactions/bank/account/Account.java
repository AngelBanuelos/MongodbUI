/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.account;

import com.masterdegree.bda.mongo.transactions.bank.transfer.Transfer;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.LinkedList;
import org.bson.Document;

/**
 *
 * @author angel_banuelos
 */
public class Account {

    private long accountID;
    private String name;
    private double balance;
    private LinkedList<Transfer> transferList = new LinkedList<Transfer>();
    private MongoCollection coll;
    private Document doc;

    public Account(long accountID, String name, float balance, MongoCollection coll) {
        this.accountID = accountID;
        this.name = name;
        this.balance = balance;
        this.coll = coll;
        addNewAccount();
    }

    public Account(Document document, MongoCollection mongoCollection) {
        this.doc = document;
        this.coll = mongoCollection;
        mapDoc();
    }

    public synchronized void addTransfer(Transfer t) {
        transferList.add(t);
        pushTransferIDInDB(t);
    }

    public synchronized void withdraw(double amount) throws NoSuffictientFunds {
        updateBalance();
        if (amount <= balance) {
            balance -= amount;
            coll.updateOne(new Document("_id", accountID), new Document("$inc", new Document("balance",
                    -amount)));
        } else {
            throw new NoSuffictientFunds("No suffictient funds in account " + accountID);
        }
    }

    public synchronized void deposit(double amount) throws InvalidAmount {
        updateBalance();
        if (amount > 0) {
            balance += amount;
            coll.updateOne(new Document("_id", accountID), new Document("$inc", new Document("balance",
                    amount)));
        } else {
            throw new InvalidAmount("Deposit amount should be always positive");
        }
    }

    public synchronized void pullTransferIDInDB(Transfer t) {
        coll.updateOne(new Document("_id", accountID), new Document("$pull", new Document("pendingTrasnfers",
                t.getId())));
    }

    private synchronized void pushTransferIDInDB(Transfer t) {
        coll.updateOne(new Document("_id", accountID), new Document("$push", new Document("pendingTrasnfers",
                t.getId())));
    }

    private synchronized Document getDocument() {
        if (doc == null) {
            Document doc = new Document();
            doc.put("_id", accountID);
            doc.put("name", name);
            doc.put("balance", balance);
            this.doc = doc;
        }
        return doc;
    }

    private synchronized void addNewAccount() {
        coll.insertOne(getDocument());
    }

    private synchronized void mapDoc() {
        if (doc != null) {
            accountID = doc.getLong("_id");
            name = doc.getString("name");
            balance = doc.getDouble("balance");
        }
    }

    private synchronized void updateBalance() {
        FindIterable<Document> d = coll.find(Filters.eq("name", name));
        for (Document document : d) {
            balance = document.getDouble("balance");
        }
    }

}
