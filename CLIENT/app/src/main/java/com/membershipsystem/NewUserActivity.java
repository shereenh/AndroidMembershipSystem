package com.membershipsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.membershipsystem.Data.DataHolder;
import com.membershipsystem.Data.DataObject;
import com.membershipsystem.Data.UserObject;

public class NewUserActivity extends AppCompatActivity {

    Button register, goBack, makeUserButton, makeAdminButton;
    EditText Ename, Eusername, Epassword, Econfirm_password;
    String name, username, password, confirm_password, phoneNum;
    boolean passwordSwitch = false, makeUser = true, phoneSwitch=false;
    String[] usernames,passwords;
    int itemPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        DataObject d = new DataObject();
        d = clientConnection.connectForInfo(d);

        String[] info = d.getMessage().split("---");

        System.out.println("This: "+info[0]+" "+info[1]);

        usernames = info[0].split(",");
        passwords = info[1].split(",");


        Ename = (EditText) findViewById(R.id.setName);
        Eusername = (EditText) findViewById(R.id.setUsername);
        Epassword = (EditText) findViewById(R.id.setPassword);
        Econfirm_password = (EditText) findViewById(R.id.setConfirmPassword);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        makeUserButton = (Button)findViewById(R.id.makeUser);
        makeUserButton.setBackgroundColor(Color.BLUE);
        makeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!makeUser){
                    makeUser = true;
                    makeUserButton.setBackgroundColor(Color.BLUE);
                    makeAdminButton.setBackgroundColor(Color.GRAY);
                }
            }
        });

        makeAdminButton = (Button)findViewById(R.id.makeAdmin);
        makeAdminButton.setBackgroundColor(Color.GRAY);
        makeAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(makeUser){
                    if(!makeAdminDialog()){
                        makeUserButton.setBackgroundColor(Color.GRAY);
                        makeAdminButton.setBackgroundColor(Color.BLUE);
                    }
                }
            }
        });

        register = (Button) findViewById(R.id.buttonReg);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Ename.getText().toString();
                username = Eusername.getText().toString();
                password = Epassword.getText().toString();
                confirm_password = Econfirm_password.getText().toString();
                phoneNum = ((EditText)findViewById(R.id.setPhone)).getText().toString();
                if(phoneNum.isEmpty()){
                    phoneNum = "000-000-0000";
                    phoneSwitch = true;
                }else if(phoneNum.length()>12){
                    phoneSwitch = false;
                    Toast.makeText(NewUserActivity.this, "Phone number too long!", Toast.LENGTH_LONG).show();
                }else{
                    phoneSwitch = true;
                }

                if (!password.equals(confirm_password)) {
                    System.out.println("Passwords don't match!");
                    Toast.makeText(NewUserActivity.this, "Passwords do not match.\nPlease try again!", Toast.LENGTH_LONG).show();
                    passwordSwitch = false;
                } else {
                    passwordSwitch = true;
                }
                if (!username.equals("") && !password.equals("") && !name.equals("") && !confirm_password.equals("") && passwordSwitch && phoneSwitch) {
                    System.out.println("Sending this to server!");
                    runThread();
                }else{
                    Toast.makeText(NewUserActivity.this, "Please Enter Required Information!\nUsername\nName\nPassword (both fields)", Toast.LENGTH_LONG).show();

                }

            }
        });

        goBack = (Button) findViewById(R.id.buttonGoBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(NewUserActivity.this, LoginActivity.class);
                startActivity(intent);
                NewUserActivity.this.finish();
            }
        });

    }

    private void runThread() {
        runOnUiThread(new Thread(new Runnable() {
            // private Runnable send = runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserObject user = new UserObject();
                user.setName(name);
                user.setUsername(username);
                user.setPassword(password);
                user.setDob(((EditText)findViewById(R.id.setDob)).getText().toString().isEmpty()?
                        "not specified":((EditText)findViewById(R.id.setDob)).getText().toString());
                user.setEmail(((EditText)findViewById(R.id.setEmail)).getText().toString().isEmpty()?
                        "not specified":((EditText)findViewById(R.id.setEmail)).getText().toString());
                user.setAddress(((EditText)findViewById(R.id.setAddress)).getText().toString().isEmpty()?
                        "not specified":((EditText)findViewById(R.id.setAddress)).getText().toString());
                user.setPhoneNum(phoneNum);

                if(makeUser){
                    user.setStatus(0);
                }else{
                    user.setStatus(4);
                }

                user = clientConnection.connectNew(user);
                if (user.getStatus() == 3 || user.getStatus() == 4) {
                    String type;
                    if(user.getStatus()==4){
                        type = "Admin";
                        DataHolder.setAdmin(true);
                    }else{
                        type = "User";
                        DataHolder.setAdmin(false);
                    }
                    Toast.makeText(NewUserActivity.this, "Welcome, "+type+" : "+user.getUsername()+" !", Toast.LENGTH_LONG).show();
                    //startActivity
                    Log.d("Jet", "Status= " + user.getStatus());
                    Intent intent = new Intent();
                    printAll(user);
                    DataHolder.setNamesArray((user.getMes()).split(","));
                    DataHolder.setUsernamesArray((user.getMessage()).split(","));
                    Bundle b = new Bundle();
                    b.putSerializable("UserObject0",user);
                    intent.putExtras(b);
                    intent.setClass(NewUserActivity.this, MainActivityAdmin.class);
                    user.setMessage("New User");
                    intent.putExtra("userObject0", user);
                    startActivity(intent);
                    NewUserActivity.this.finish();
                } else if (user.getStatus() == 5) {
                    Log.d("Jet", "Status= " + user.getStatus());
                    Toast.makeText(NewUserActivity.this, "Username already exists, please try another one!", Toast.LENGTH_LONG).show();

                } else if(user.getStatus() == 6){

                    Toast.makeText(NewUserActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("Jet", "no match");
                }
            }
        }));

    }

    void printAll(UserObject u){
        System.out.println(u.getUsername()+" "+u.getName()+" "+u.getDob()
                +" "+u.getPhoneNum()+" "+u.getEmail()+" "+u.getAddress());
    }

    boolean makeAdminDialog(){


        // custom dialog
        final Dialog dialog = new Dialog(NewUserActivity.this);
        dialog.setContentView(R.layout.dialog_make_admin);
       // dialog.setTitle("Title...");

        final ListView listView = (ListView)dialog.findViewById(R.id.adminList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, usernames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition  = position;
                setItemPos(position);

                // ListView Clicked item value
                String  itemValue = (String) listView.getItemAtPosition(position);

                view.setBackgroundColor(Color.GRAY);

                // Show Alert
//                Toast.makeText(getApplicationContext(),
//                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
//                        .show();

            }

        });


        Button dialogButton = dialog.findViewById(R.id.makeAdminButton);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               System.out.println("Button clicked");
               String enteredPassword = ((EditText)dialog.findViewById(R.id.editAdminPassword)).getText().toString();
                System.out.println(enteredPassword+" "+passwords[itemPos]);

                if(enteredPassword.equals(passwords[itemPos])){
                makeUser = false;
                    Toast.makeText(getApplicationContext(),
                            "Admin Made!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
               }else{
                    Toast.makeText(getApplicationContext(),
                            "Wrong Password!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        return makeUser;
    }

    void setItemPos(int i){
        itemPos = i;
    }
}
