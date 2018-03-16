/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo;

import com.masterdegree.bda.mongo.ui.frame.Main;

/**
 *
 * @author angel_banuelos
 */
public class MongoDBGUI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Thread(new Runnable() {

            @Override
            public void run() {
                Main main = new Main();
                main.setVisible(true);
            }
        }).start();

    }

}
