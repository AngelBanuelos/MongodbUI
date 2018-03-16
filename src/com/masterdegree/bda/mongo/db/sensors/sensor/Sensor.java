/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.db.sensors.sensor;

import com.masterdegree.bda.mongo.db.sensors.utils.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel_banuelos
 */
public class Sensor implements Runnable {

    private Type type;
    private Object value;
    private volatile boolean stop;

    public enum Type {

        PHOTOELECTRIC,
        HUMIDITY,
        TEMPERATURE

    }

    public Sensor(Type type) {
        this.type = type;
    }

    public long getTimeInMiliseconds() {
        long time = -1;
        switch (type) {
            case HUMIDITY:
                time = Utils.rndLong(200, 250);
                break;
            case PHOTOELECTRIC:
                time = Utils.rndLong(250, 200);
                break;
            case TEMPERATURE:
                time = Utils.rndLong(150, 200);
                break;
        }
        return time;
    }

    private synchronized Object setValue() {
        switch (type) {
            case HUMIDITY:
                value = Utils.rndDouble(0, 100);
                break;
            case PHOTOELECTRIC:
                value = Utils.rndLong(1000, 5000);
                break;
            case TEMPERATURE:
                value = Utils.rndDouble(15, 30);
                break;
        }
        return value;
    }

    public synchronized void resetValue() {
        value = null;
    }

    public Object getValue() {
        return value;
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            setValue();
            try {
                Thread.sleep(getTimeInMiliseconds());
            } catch (InterruptedException ex) {
                Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Sensor stopped : " + type);
    }
}
