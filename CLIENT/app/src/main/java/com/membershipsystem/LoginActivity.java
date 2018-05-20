package com.membershipsystem;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.membershipsystem.Data.DataHolder;
import com.membershipsystem.Data.DataObject;
import com.membershipsystem.Data.UserObject;

public class LoginActivity extends AppCompatActivity {

    Button nextPage, newButton;
    EditText Eusername, Epassword;
    String username, password;
    Thread tSend;
    UserObject user;
    DataObject initialUser;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        getActionBar().setTitle("Login");
//        getSupportActionBar().setTitle("Login");  // provide compatibility to all the versions

//        if (isMyServiceRunning(serverListener.class)) {
//            //User is logged in already, so start mainactivity
//            System.out.println("logged in already");
//            intent = new Intent();
//            intent.setClass(LoginActivity.this, LoginActivity.class);
//            startActivity(intent);
//            LoginActivity.this.finish();
//        } else {
            // else start loginactivity
            System.out.println("not logged in yet.");
            setContentView(R.layout.activity_login);
            Eusername = findViewById(R.id.editTextUsername);
            Epassword = findViewById(R.id.editTextPassword);

            newButton = findViewById(R.id.button_new);
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, NewUserActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    System.out.println("New user!");
                }
            });


            nextPage = findViewById(R.id.buttonNextPage);
            nextPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    username = Eusername.getText().toString();
                    password = Epassword.getText().toString();
                    if (!username.equals("") && !password.equals("")) {
                        System.out.println("Starting up!");
                        // new Thread(send).start();
                        runThread();
                    }
                }
            });
        //}
    }

    private void runThread() {
        runOnUiThread(new Thread(new Runnable() {
            // private Runnable send = runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String up = username + "," + password;
                System.out.println(up);
                initialUser  = new DataObject();
                initialUser.setMessage(up);
                user = clientConnection.connect(initialUser);

                Log.d("Jet", "Get something");
               if(user.getStatus() == 4 || user.getStatus() == 3 ){
                    String type;
                    if(user.getStatus()==4){
                        type = "Admin";
                        DataHolder.setAdmin(true);
                    }else{
                        type = "User";
                        DataHolder.setAdmin(false);
                    }
                    Toast.makeText(LoginActivity.this, "Welcome, "+type+" : "+user.getUsername()+" !", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    printAll(user);
                    DataHolder.setNamesArray((user.getMes()).split(","));
                    DataHolder.setUsernamesArray((user.getMessage()).split(","));
                    Bundle b = new Bundle();
                    b.putSerializable("UserObject0",user);
                    intent.putExtras(b);
                    intent.setClass(LoginActivity.this, MainActivityAdmin.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }else {
                    Log.d("Jet", "no match");
                    Toast.makeText(LoginActivity.this, "Error logging in!", Toast.LENGTH_LONG).show();

                }
            }
        }));

    }

    void setExtras() {
        String[] holder = user.getMessage().split("-");
        String buddy_list = holder[1];
        String request_list = holder[2];
        String sent_list = holder[0];
        System.out.println("Buddy List recieved: " + buddy_list);
        intent.putExtra("Buddies0", buddy_list);
        System.out.println("Requests recieved: " + request_list);
        intent.putExtra("Requests0", request_list);
        System.out.println("Sent List recieved: " + sent_list);
        intent.putExtra("Sent0", sent_list);
    }


    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void printAll(UserObject u){
        System.out.println(u.getUsername()+" "+u.getName()+" "+u.getDob()
            +" "+u.getPhoneNum()+" "+u.getEmail()+" "+u.getAddress());
    }
}
