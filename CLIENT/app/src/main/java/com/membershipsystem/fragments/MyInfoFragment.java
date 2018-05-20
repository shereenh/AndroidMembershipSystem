package com.membershipsystem.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.membershipsystem.Data.DataHolder;
import com.membershipsystem.Data.UserObject;
import com.membershipsystem.LoginActivity;
import com.membershipsystem.MainActivityAdmin;
import com.membershipsystem.R;
import com.membershipsystem.clientConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInfoFragment extends Fragment {

    Button editButton, deleteButton;
    View rootView;
    UserObject user;

    public MyInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_info_, container, false);
        editButton = rootView.findViewById(R.id.edit_button);
//        saveButton.setEnabled(false);

        Bundle b = getActivity().getIntent().getExtras();
        if(b != null){
            user = (UserObject)b.getSerializable("UserObject0");
            System.out.println("Here too!");
            //printAll(user);
        }
        setAll();
        System.out.println("Status "+user.getStatus());
        if(DataHolder.isAdmin()){
            ((TextView) rootView.findViewById(R.id.status)).setText("ADMINISTRATOR");
        }else {
            ((TextView) rootView.findViewById(R.id.status)).setText("USER");
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            createDialog();

            }
        });

        deleteButton = rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    deleteUser();
            }
        });
        return rootView;
    }

    void printAll(UserObject u){
        System.out.println(u.getUsername()+"\n"+u.getName()+"\n"+u.getDob()
                +"\n"+u.getPhoneNum()+"\n"+u.getEmail()+"\n"+u.getAddress());
    }

    void createDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_info);
        dialog.setTitle("Title...");

        ((TextView) dialog.findViewById(R.id.editUsername)).setText(user.getUsername());
        ((TextView) dialog.findViewById(R.id.editName)).setText(user.getName());
        ((TextView) dialog.findViewById(R.id.editdob)).setText(user.getDob());
        ((TextView) dialog.findViewById(R.id.editphone)).setText(user.getPhoneNum());
        ((TextView) dialog.findViewById(R.id.editemail)).setText(user.getEmail());
        ((TextView) dialog.findViewById(R.id.editaddress)).setText(user.getAddress());

        dialog.findViewById(R.id.editUsername).setFocusable(false);

        String h = ((TextView) dialog.findViewById(R.id.editUsername)).getText().toString();

        // set the custom dialog components - text, image and button
//        TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Android custom dialog example!");
//        ImageView image = (ImageView) dialog.findViewById(R.id.image);
//        image.setImageResource(R.drawable.ic_launcher);

        Button dialogButton = dialog.findViewById(R.id.saveButton);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val;
                String currentName, currentDob, currentPhone, currentEmail, currentAddress;

                if(!(currentName=((TextView) dialog.findViewById(R.id.editName)).getText().toString()).equals(user.getName()))
                    val = "1"; else val = "0";
                if(!(currentDob=((TextView) dialog.findViewById(R.id.editdob)).getText().toString()).equals(user.getDob()))
                    val += "1"; else val += "0";
                if(!(currentPhone=((TextView) dialog.findViewById(R.id.editphone)).getText().toString()).equals(user.getPhoneNum()))
                    val += "1"; else val += "0";
                if(!(currentEmail=((TextView) dialog.findViewById(R.id.editemail)).getText().toString()).equals(user.getEmail()))
                    val += "1"; else val += "0";
                if(!(currentAddress=((TextView) dialog.findViewById(R.id.editaddress)).getText().toString()).equals(user.getAddress()))
                    val += "1"; else val += "0";

                String[] allVals = val.split("");
                System.out.println("Changed: "+val);

                if(val.contains("1")){

                    if(allVals[1].equals("1"))  user.setName(currentName);
                    if(allVals[2].equals("1"))  user.setDob(currentDob);
                    if(allVals[3].equals("1"))  user.setPhoneNum(currentPhone);
                    if(allVals[4].equals("1"))  user.setEmail(currentEmail);
                    if(allVals[5].equals("1"))  user.setAddress(currentAddress);

                    printAll(user);

                    user.setOperation("Updated");
                    user = clientConnection.sendToServer(user);
                    setAll();
                    Toast.makeText(getActivity(), user.getOperation()+"!", Toast.LENGTH_SHORT).show();

                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void deleteUser(){

        final CharSequence choices[] = new CharSequence[]{"Yes","No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?");
        builder.setItems(choices,new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                if(which==0){       //DELETE
                    System.out.println("Result: "+choices[0]);
                    user.setMes(user.getUsername());
                    user.setOperation("Delete Myself");
                    user = clientConnection.sendToServer(user);
                    System.out.println("RES "+user.getOperation());
                    if(user.getMes().equals("Delete Successful")){
                        Toast.makeText(getActivity(), "Good-bye then!", Toast.LENGTH_LONG).show();
                        clientConnection.close();
                        Intent intent = new Intent();
                        intent.setClass( getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
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

    void setAll(){
        ((TextView) rootView.findViewById(R.id.savedUsername)).setText(user.getUsername());
        ((TextView) rootView.findViewById(R.id.savedName)).setText(user.getName());
        ((TextView) rootView.findViewById(R.id.savedDob)).setText(user.getDob());
        ((TextView) rootView.findViewById(R.id.savedNum)).setText(user.getPhoneNum());
        ((TextView) rootView.findViewById(R.id.savedEmail)).setText(user.getEmail());
        ((TextView) rootView.findViewById(R.id.savedAddress)).setText(user.getAddress());
    }
}
