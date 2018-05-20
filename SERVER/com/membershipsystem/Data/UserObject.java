package com.membershipsystem.Data;

import java.io.Serializable;

public class UserObject extends DataObject implements Serializable{

	/**
	 * Class used to save information passed between client and server
	 */
	private static final long serialVersionUID = 1L;
	
	int status;
    int userID;
    String operation;
    String username;
    String password;
    String mes;
    String name, dob, phoneNum, email, address;
    
	
    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }

    public void setUserID(int userID){ this.userID = userID; }

    public int getUserID(){ return userID; }

    public void setOperation(String operation){
        this.operation = operation;
    }

    public String getOperation(){
        return operation;
    }

    public void setUsername(String username){ this.username = username; }

    public String getUsername(){ return username; }

    public void setPassword(String password){ this.password = password; }

    public String getPassword(){ return password; }

    public void setMes(String mes){ this.mes = mes; }

    public String getMes(){ return mes; }

    public void setName(String name){ this.name = name; }

    public String getName(){ return name; }

    public void setDob(String dob){ this.dob = dob; }

    public String getDob(){ return dob; }

    public void setPhoneNum(String phoneNum){ this.phoneNum = phoneNum; }

    public String getPhoneNum(){ return phoneNum; }

    public void setEmail(String email){ this.email = email; }

    public String getEmail(){ return email; }

    public void setAddress(String address){ this.address = address; }

    public String getAddress(){ return address; }

	
}
