package com.membershipsystem.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.membershipsystem.Data.DataHolder;
import com.membershipsystem.Data.UserObject;
import com.membershipsystem.LoginActivity;
import com.membershipsystem.R;
import com.membershipsystem.clientConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    UserObject user, searchedUser;
    boolean namesState = false;

    ArrayAdapter<String> adapterUsernames,adapterNames;

    private AutoCompleteTextView namesAutoComplete, usernamesAutoComplete;
    View rootView;
    LinearLayout linearLayout;
    Button changeSearchButton, searchButton, editButton, deleteButton;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        Bundle b = getActivity().getIntent().getExtras();
        if(b != null){
            user = (UserObject)b.getSerializable("UserObject0");
        }



        namesAutoComplete = rootView.findViewById(R.id.namesAutoComplete);

        adapterNames = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_list_item_1, DataHolder.getNamesArray());
        namesAutoComplete.setAdapter(adapterNames);
        namesAutoComplete.setThreshold(1);

        usernamesAutoComplete = rootView.findViewById(R.id.usernamesAutoComplete);

        adapterUsernames = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_list_item_1, DataHolder.getUsernamesArray());
        usernamesAutoComplete.setAdapter(adapterUsernames);
        usernamesAutoComplete.setThreshold(1);
        // Inflate the layout for this fragment

        linearLayout = rootView.findViewById(R.id.searchLayout);
        linearLayout.setVisibility(View.INVISIBLE);


        // Edit Button only for Admin
        editButton = (Button) rootView.findViewById(R.id.edit_button);
        if(!DataHolder.isAdmin()){
            editButton.setVisibility(View.INVISIBLE);
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        //Delete button only for Admin
        deleteButton = (Button) rootView.findViewById(R.id.delete_button);
        if(!DataHolder.isAdmin()){
            deleteButton.setVisibility(View.INVISIBLE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });



        // Search for person's information
        searchButton = rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus())
                        ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                searchInfo();
            }
        });

        // Search by username or name
        changeSearchButton = (Button) rootView.findViewById(R.id.changeSearch);
        changeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                changeSelected();
            }
        });

        changeSelected();

        return rootView;
    }

    void changeSelected(){

        if(namesState){
            namesState = false;
            changeSearchButton.setBackgroundColor(Color.BLUE);
            namesAutoComplete.setVisibility(View.INVISIBLE);
            usernamesAutoComplete.setVisibility(View.VISIBLE);
            changeSearchButton.setText("U");
        } else {
            namesState = true;
            changeSearchButton.setBackgroundColor(Color.GREEN);
            usernamesAutoComplete.setVisibility(View.INVISIBLE);
            namesAutoComplete.setVisibility(View.VISIBLE);
            changeSearchButton.setText("N");
        }
    }

    void setAll(UserObject u){
        ((TextView) rootView.findViewById(R.id.searchUsername)).setText(u.getUsername());
        ((TextView) rootView.findViewById(R.id.searchName)).setText(u.getName());
        ((TextView) rootView.findViewById(R.id.searchDob)).setText(u.getDob());
        ((TextView) rootView.findViewById(R.id.searchNum)).setText(u.getPhoneNum());
        ((TextView) rootView.findViewById(R.id.searchEmail)).setText(u.getEmail());
        ((TextView) rootView.findViewById(R.id.searchAddress)).setText(u.getAddress());
    }

    void createDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_info);
        dialog.setTitle("Title...");

        ((TextView) dialog.findViewById(R.id.editUsername)).setText(searchedUser.getUsername());
        ((TextView) dialog.findViewById(R.id.editName)).setText(searchedUser.getName());
        ((TextView) dialog.findViewById(R.id.editdob)).setText(searchedUser.getDob());
        ((TextView) dialog.findViewById(R.id.editphone)).setText(searchedUser.getPhoneNum());
        ((TextView) dialog.findViewById(R.id.editemail)).setText(searchedUser.getEmail());
        ((TextView) dialog.findViewById(R.id.editaddress)).setText(searchedUser.getAddress());

        dialog.findViewById(R.id.editUsername).setFocusable(false);

        String h = ((TextView) dialog.findViewById(R.id.editUsername)).getText().toString();


        Button dialogButton = dialog.findViewById(R.id.saveButton);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val;
                String currentName, currentDob, currentPhone, currentEmail, currentAddress;

                if(!(currentName=((TextView) dialog.findViewById(R.id.editName)).getText().toString()).equals(searchedUser.getName()))
                    val = "1"; else val = "0";
                if(!(currentDob=((TextView) dialog.findViewById(R.id.editdob)).getText().toString()).equals(searchedUser.getDob()))
                    val += "1"; else val += "0";
                if(!(currentPhone=((TextView) dialog.findViewById(R.id.editphone)).getText().toString()).equals(searchedUser.getPhoneNum()))
                    val += "1"; else val += "0";
                if(!(currentEmail=((TextView) dialog.findViewById(R.id.editemail)).getText().toString()).equals(searchedUser.getEmail()))
                    val += "1"; else val += "0";
                if(!(currentAddress=((TextView) dialog.findViewById(R.id.editaddress)).getText().toString()).equals(searchedUser.getAddress()))
                    val += "1"; else val += "0";

                String[] allVals = val.split("");
                System.out.println("Changed: "+val);

                if(val.contains("1")){

                    if(allVals[1].equals("1"))  searchedUser.setName(currentName);
                    if(allVals[2].equals("1"))  searchedUser.setDob(currentDob);
                    if(allVals[3].equals("1"))  searchedUser.setPhoneNum(currentPhone);
                    if(allVals[4].equals("1"))  searchedUser.setEmail(currentEmail);
                    if(allVals[5].equals("1"))  searchedUser.setAddress(currentAddress);

                    //printAll(user);

                    searchedUser.setOperation("Updated");
                    searchedUser = clientConnection.sendToServer(searchedUser);
                    setAll(searchedUser);
                    Toast.makeText(getActivity(), searchedUser.getOperation()+"!", Toast.LENGTH_SHORT).show();

                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void searchInfo(){

        String query;
        if(namesState){
            query = "name:" + namesAutoComplete.getText().toString();
        }else{
            query = "username:" + usernamesAutoComplete.getText().toString();

        }
        System.out.println("Query: "+query);
        user.setOperation("Search");
        user.setMes(query);
        user = clientConnection.sendToServer(user);
        if(user.getOperation().equals("Search Result")){
            System.out.println("Results: "+user.getMes());
            searchedUser = new UserObject();
            String[] temp = user.getMes().split("-@-");
            searchedUser.setUsername(temp[0]);
            searchedUser.setName(temp[1]);
            searchedUser.setDob(temp[2]);
            searchedUser.setPhoneNum(temp[3]);
            searchedUser.setEmail(temp[4]);
            searchedUser.setAddress(temp[5]);
            setAll(searchedUser);
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getActivity(), user.getOperation(), Toast.LENGTH_LONG).show();
        }

    }

    void deleteUser(){

        final CharSequence choices[] = new CharSequence[]{"Yes","No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?");
        builder.setItems(choices,new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                if(which==0){       //DELETE
                    System.out.println("Result: "+choices[0]);
                    user.setMes(searchedUser.getUsername());
                    user.setOperation("Delete by Admin");
                    user = clientConnection.sendToServer(user);
                    System.out.println("RES "+user.getMes());
                    if(user.getMes().equals("Delete Successful")){
                        Toast.makeText(getActivity(), searchedUser.getUsername()+" deleted successfully!", Toast.LENGTH_LONG).show();

                        System.out.println("Woah: "+user.getMessage());

                        String[] hold = user.getMessage().split("---");

                        DataHolder.setNamesArray(hold[0].split(","));
                        DataHolder.setUsernamesArray(hold[1].split(","));

                        System.out.println("Woah:"+searchedUser.getName()+searchedUser.getUsername());

                        namesAutoComplete.setText("");
                        usernamesAutoComplete.setText("");
                        linearLayout.setVisibility(View.INVISIBLE);


                        adapterNames = new ArrayAdapter<String>
                                (getActivity(),android.R.layout.simple_list_item_1, DataHolder.getNamesArray());
                        namesAutoComplete.setAdapter(adapterNames);
                        namesAutoComplete.setThreshold(1);

                        usernamesAutoComplete = rootView.findViewById(R.id.usernamesAutoComplete);

                        adapterUsernames = new ArrayAdapter<String>
                                (getActivity(),android.R.layout.simple_list_item_1, DataHolder.getUsernamesArray());
                        usernamesAutoComplete.setAdapter(adapterUsernames);
                        usernamesAutoComplete.setThreshold(1);


                    }else {
                        Toast.makeText(getActivity(), "Delete Unsuccessful!", Toast.LENGTH_LONG).show();
                    }
                }
                if(which==1){
                    System.out.println("Result: "+choices[1]);

                }
            }
        });builder.show();

    }

}
