package com.membershipsystem.Data;

import java.io.Serializable;

/**
 * Created by shereen on 12/7/2017.
 */

public class DataObject implements Serializable{

    private static final long serialVersionUID = 1L;

    protected String message;

    public DataObject(){
        message = "";
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String inMessage){
        message = inMessage;
    }
}
