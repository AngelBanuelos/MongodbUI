/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.db.sensors.utils;

/**
 *
 * @author angel_banuelos
 */
public class Utils {

    public static long rndLong(int min, int max) {
        return (long) (min + (max - min) * Math.random());
    }

    public static double rndDouble(int min, int max) {
        return min + (max - min) * Math.random();
    }

    public static int rndInt(int min, int max) {
        return (int) (min + (max - min) * Math.random());
    }
}
