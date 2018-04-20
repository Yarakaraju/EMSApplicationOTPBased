/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.util;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
public class CustomSucessType {
 
    private String message;

    public CustomSucessType(String message){
        this.message = message;
    }

    public String getErrorMessage() {
        return message;
    }   
}
