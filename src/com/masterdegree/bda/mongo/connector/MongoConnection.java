/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.connector;

import com.mongodb.MongoClient;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel_banuelos
 */
public class MongoConnection {

    private String user;
    private char[] password;
    private String address;
    private int port;
    private static MongoConnection connection;
    private static MongoClient mongoConection = null;

    private MongoConnection(String user, char[] password, String address, int port) throws Exception {
        this.user = user;
        this.password = password;
        this.address = address;
        this.port = port;
        createConnection();
    }

    private MongoConnection() {
        try {
            createConnection();
        } catch (Exception ex) {
            Logger.getLogger(MongoConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createConnection() throws Exception {
        if (address != null && port != 0 && !address.isEmpty()) {
            mongoConection = new MongoClient(address, port);
        } else {
            mongoConection = new MongoClient("localhost", 27017);
        }

    }

    public static MongoConnection createInstance(String user, char[] password, String address, int port) throws Exception {
        if (connection == null) {
            connection = new MongoConnection(user, password, address, port);
        }
        return connection;
    }

    public static MongoConnection createInstance() {
        if (connection == null) {
            connection = new MongoConnection();
        }
        return connection;
    }

    public MongoClient connection() {
        return mongoConection;
    }

}
