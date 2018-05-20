package com.membershipsystem;

import com.membershipsystem.Data.DataObject;
import com.membershipsystem.Data.UserObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by shereen on 12/7/2017.
 */

public class clientConnection {

    protected static Socket socketToServer = null;
    protected static ObjectInputStream IN = null;
    protected static ObjectOutputStream OUT = null;
    protected static UserObject savedUser = new UserObject();

    protected static String ipAddress = "192.168.56.1"; //"192.168.56.1" "implementation"
    protected static int port = 3210;

    public clientConnection() {
    }
    public static DataObject connectForInfo(DataObject infoUser) {

        infoUser.setMessage("Admin Info Request");
        try {
            socketToServer = new Socket(ipAddress, port); //128.235.208.201
            //socketToServer = new Socket("128.235.208.201", 3210);
            OUT = new ObjectOutputStream(socketToServer.getOutputStream());
            IN = new ObjectInputStream(socketToServer.getInputStream());
            OUT.writeObject(infoUser);
//            OUT.writeUnshared(user);
//            OUT.flush();

            infoUser = (DataObject) IN.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoUser;
    }


    public static UserObject connect(DataObject initialUser) {
        UserObject user = new UserObject();
        String credentials = "";
        try {
            socketToServer = new Socket(ipAddress, port); //128.235.208.201
            //socketToServer = new Socket("128.235.208.201", 3210);
            OUT = new ObjectOutputStream(socketToServer.getOutputStream());
            IN = new ObjectInputStream(socketToServer.getInputStream());
            OUT.writeObject(initialUser);
//            OUT.writeUnshared(user);
//            OUT.flush();

            user = (UserObject) IN.readObject();
            if (user.getStatus() == 3 || user.getStatus() == 4) {
                System.out.println("Recieved from server");
                savedUser.setUserID(user.getUserID());
                savedUser.setUsername(user.getUsername());
                savedUser.setPassword(user.getPassword());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static UserObject connectNew(UserObject firstObject) {
        DataObject dataObject = new DataObject();
        try {
            socketToServer = new Socket(ipAddress, port); //128.235.208.201
            //socketToServer = new Socket("128.235.208.201", 3210);
            OUT = new ObjectOutputStream(socketToServer.getOutputStream());
            IN = new ObjectInputStream(socketToServer.getInputStream());
            dataObject.setMessage("New User");
            OUT.writeObject(dataObject);
//            OUT.writeUnshared(user);
//            OUT.flush();


            dataObject = (DataObject) IN.readObject();
            if (dataObject.getMessage().equals("Send Credentials")) {
                firstObject.setMessage("Credentials Sent");
                OUT.writeObject(firstObject);
            }

            firstObject = (UserObject) IN.readObject();
            if (firstObject.getStatus() == 3 ) {
                System.out.println("Recieved from server");
                savedUser.setUserID(firstObject.getUserID());
                savedUser.setUsername(firstObject.getUsername());
                savedUser.setPassword(firstObject.getPassword());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return firstObject;
    }

    public static UserObject sendToServer(UserObject user) {
        try {
//            user.setUserID(savedUser.getUserID());
//            user.setUsername(savedUser.getUsername());
//            user.setPassword(savedUser.getPassword());
//            System.out.println("Sent OUT:\nID: "+user.getUserID()+"\nUsername: "+user.getUsername()+
//                    "\nOperation: "+user.getOperation()+"\nMessage: "+user.getMessage());
            //OUT.writeObject(user);
            OUT.writeUnshared(user);
            // OUT.reset();
            OUT.flush();

            user = (UserObject) IN.readObject();
            if (user != null) {
                System.out.println("Recieved from server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void close() {
        try {
            socketToServer.close();
            IN.close();
            OUT.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Socket getSocket() {
        return socketToServer;
    }

    public static ObjectInputStream getInputStream() {
        return IN;
    }

    public static ObjectOutputStream getOutputStream() {
        return OUT;
    }

    public static UserObject getUserObject() {
        return savedUser;
    }
}
