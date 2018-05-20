package com.membershipsystem.Data;

/**
 * Created by shereen on 12/17/2017.
 */

public class DataHolder {

    static String[] namesArray;
    static String[] usernamesArray;
    static boolean admin;

    public static void setAdmin(boolean isA){ admin = isA; }

    public static boolean isAdmin(){ return admin; }

    public static String[] getNamesArray(){
        return namesArray;
    }

    public static String[] getUsernamesArray(){
        return usernamesArray;
    }

    public static void setNamesArray(String[] nA){
        namesArray = nA;
    }

    public static void setUsernamesArray(String[] uA){
        usernamesArray = uA;
    }


}
