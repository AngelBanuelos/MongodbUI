/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.db.sensors.node;

import com.masterdegree.bda.mongo.db.sensors.sensor.Sensor;
import com.mongodb.client.MongoCollection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 * @author angel_banuelos
 */
public class Node implements Runnable {

    private final MongoCollection coll;
    private long id;
    private volatile boolean stop;

    public Node(MongoCollection coll, long id) {
        this.coll = coll;
        this.id = id;
    }

    private synchronized void save(Object photoElectric, Object humidity, Object temperature) {
        Document document = new Document();
        long time = System.currentTimeMillis();
        document.append("node_id", id);
        document.append("time", time);
        if (photoElectric != null) {
            document.append("photoElectric", (long) photoElectric);
        }
        if (humidity != null) {
            document.append("humidity", (double) humidity);
        }
        if (temperature != null) {
            document.append("temperature", (double) temperature);
        }
        coll.insertOne(document);
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        System.out.println("Node started " + id);
        final Sensor humidity = new Sensor(Sensor.Type.HUMIDITY);
        final Sensor photoelectric = new Sensor(Sensor.Type.PHOTOELECTRIC);
        final Sensor temperature = new Sensor(Sensor.Type.TEMPERATURE);
        Thread t1 = new Thread(humidity);
        Thread t2 = new Thread(photoelectric);
        Thread t3 = new Thread(temperature);
        t1.start();
        t2.start();
        t3.start();
        while (!stop) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
            }
            save(photoelectric.getValue(), humidity.getValue(), temperature.getValue());
            humidity.resetValue();
            photoelectric.resetValue();
            temperature.resetValue();
        }
        System.out.println("Node Id stopped: " + id);
        humidity.stop();
        photoelectric.stop();
        temperature.stop();
    }

}
