/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.db.sensors;

import com.masterdegree.bda.mongo.db.sensors.node.Node;
import com.mongodb.client.MongoCollection;

/**
 *
 * @author angel_banuelos
 */
public class Main implements Runnable {

    public final MongoCollection coll;
    private volatile boolean stop;

    public Main(MongoCollection coll) {
        this.coll = coll;
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
//        for (int i = 1; i < 6; i++) {
//            Thread t = new Thread(new Node(coll, i));
//            t.start();
//        }
        Node n1 = new Node(coll, 1);
        Thread t1 = new Thread(n1);
        Node n2 = new Node(coll, 2);
        Thread t2 = new Thread(n2);
        Node n3 = new Node(coll, 3);
        Thread t3 = new Thread(n3);
        Node n4 = new Node(coll, 4);
        Thread t4 = new Thread(n4);
        Node n5 = new Node(coll, 5);
        Thread t5 = new Thread(n5);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        while (!stop) ;
        System.out.println("stopping nodes");
        n1.stop();
        n2.stop();
        n3.stop();
        n4.stop();
        n5.stop();
    }

}
