package com.membershipsystem.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.membershipsystem.Data.UserObject;
import com.membershipsystem.R;
import com.membershipsystem.clientConnection;

/**
 * Created by shereen on 12/18/2017.
 */

public class EditInfoDialog {

    static UserObject u;

    public static UserObject createDialog(final UserObject user, final Activity activity){

        // custom dialog
        final Dialog dialog = new Dialog(activity);
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

                    //printAll(user);

                    user.setOperation("Updated");
                    u = clientConnection.sendToServer(user);
                    //setAll();
                    Toast.makeText(activity, u.getOperation()+"!", Toast.LENGTH_SHORT).show();

                }

                dialog.dismiss();
            }
        });

        dialog.show();
        return u;

    }
}
