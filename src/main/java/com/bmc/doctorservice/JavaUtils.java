package com.bmc.doctorservice;


public class JavaUtils {

    public static String randomLong(){
        return String.valueOf(Math.floor(100000+Math.random()*900000));
    }
}
