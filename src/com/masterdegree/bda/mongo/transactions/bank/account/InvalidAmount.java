/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.masterdegree.bda.mongo.transactions.bank.account;

/**
 *
 * @author angel_banuelos
 */
public class InvalidAmount extends Exception {

    public InvalidAmount(String text) {
        super(text);
    }

}
