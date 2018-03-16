/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.transfer;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author angel_banuelos
 */
public class Transfer {

    private long id;
    private String source;
    private String destination;
    private double amount;
    private State state;
    private Date lastUpdate;
    private MongoCollection coll;
    private Document doc;

    public Transfer(Document document, MongoCollection mongoCollection) {
        this.coll = mongoCollection;
        this.doc = document;
        mapDoc();
    }

    public enum State {
        NEW("NEW"),
        PENDING("PENDING"),
        APPLIED("APPLIED"),
        DONE("DONE"),
        ERROR("ERROR"),
        CANCELLING("CANCELLING"),
        CANCELLED_NO_SUFFICIENT_FOUDS("CANCELLED_FUNDS"),
        ERROR_EXPIRED("ERROR_EXPIRED");

        private String text;

        private State(String text) {
            this.text = text;
        }

        public String getEnumValue() {
            return text;
        }

    }

    public Transfer(long id, String source, String destination, double amount, MongoCollection coll) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.state = State.NEW;
        this.coll = coll;
        insertIntoDB();
    }

    public synchronized State getState() {
        return state;
    }

    public synchronized void setState(State state) {
        this.state = state;
    }

    public synchronized Date getLastUpdate() {
        return lastUpdate;
    }

    public synchronized void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public synchronized long getId() {
        return id;
    }

    public synchronized String getSource() {
        return source;
    }

    public synchronized String getDestination() {
        return destination;
    }

    public synchronized double getAmount() {
        return amount;
    }

    private synchronized void insertIntoDB() {
        coll.insertOne(getDocument());
    }

    private synchronized Document getDocument() {
        Document doc = new Document();
        doc.put("_id", id);
        doc.put("source", source);
        doc.put("destination", destination);
        doc.put("amount", amount);
        doc.put("state", state.getEnumValue());
        doc.put("lastUpdate", new Date());
        return doc;
    }

    public synchronized void updateStateInDB() {
        coll.updateOne(new Document("_id", id), new Document("$set", new Document("state",
                state.getEnumValue())).append("$currentDate", new Document("lastUpdate", true)));
    }

    private synchronized void mapDoc() {
        if (doc != null) {
            id = doc.getLong("_id");
            source = doc.getString("source");
            destination = doc.getString("destination");
            amount = doc.getDouble("amount");
            lastUpdate = doc.getDate("lastUpdate");
            state = State.valueOf(doc.getString("state"));
        }
    }

    public synchronized boolean timeExpired() {
        FindIterable<Document> d = coll.find(Filters.eq("_id", id));
        for (Document document : d) {
            lastUpdate = document.getDate("lastUpdate");
        }
        if ((System.currentTimeMillis() - lastUpdate.getTime()) > (1000 * 10)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transfer) {
            if (((Transfer) obj).getId() == this.id) {
                return true;
            }
        }
        return false;
    }

}
