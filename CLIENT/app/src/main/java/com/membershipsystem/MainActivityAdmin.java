package com.membershipsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.membershipsystem.Data.UserObject;
import com.membershipsystem.fragments.MyInfoFragment;
import com.membershipsystem.fragments.SearchFragment;

public class MainActivityAdmin extends AppCompatActivity {

    MyInfoFragment myInfoFragment;
    SearchFragment searchFragment;
    UserObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        Bundle b = this.getIntent().getExtras();
        if(b != null){
            user = (UserObject)b.getSerializable("UserObject0");
            System.out.println("Works!");
            printAll(user);
        }

        myInfoFragment =  new MyInfoFragment();
        //myInfoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frag_container, myInfoFragment).commit();

    }

    void printAll(UserObject u){
        System.out.println(u.getUsername()+" "+u.getName()+" "+u.getDob()
                +" "+u.getPhoneNum()+" "+u.getEmail()+" "+u.getAddress());
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tester, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            myInfoFragment =  new MyInfoFragment();
            //myInfoFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.frag_container, myInfoFragment).commit();
        }else if (id == R.id.mybutton1) {
            searchFragment =  new SearchFragment();
            //myInfoFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.frag_container, searchFragment).commit();
        }else if(id == R.id.log_out){
            user.setOperation("Log Out");
            user = clientConnection.sendToServer(user);
            System.out.println("Last operation: "+user.getOperation());
            if(user.getOperation().equals("Logged Out")){

                clientConnection.close();
                Intent intent = new Intent();
                intent.setClass( MainActivityAdmin.this, LoginActivity.class);
                startActivity(intent);
                MainActivityAdmin.this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
