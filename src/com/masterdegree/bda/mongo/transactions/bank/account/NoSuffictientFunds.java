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
public class NoSuffictientFunds extends Exception {

    public NoSuffictientFunds(String string) {
        super(string);
    }
    
}
