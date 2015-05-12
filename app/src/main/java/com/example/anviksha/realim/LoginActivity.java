package com.example.anviksha.realim;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends ActionBarActivity {

    public static String userName;
    public String userId;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(ParseUser.getCurrentUser() != null)
            ParseUser.logOut();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }


    public void onJoinRoom(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View loginPopup = li.inflate(R.layout.login_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(loginPopup);

        final EditText userInput = (EditText) loginPopup
                .findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                userName = userInput.getText().toString();
                                login();
                                progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.setTitle("Joining...");
                                progressDialog.setMessage("Please wait");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(getBaseContext(),"User not created ",Toast.LENGTH_LONG);
                } else {
                    progressDialog.dismiss();
                    userId = ParseUser.getCurrentUser().getObjectId();
                    Intent chatActivity = new Intent(getBaseContext(), RealIMActivity.class);
                    startActivity(chatActivity);
                }
            }
        });
    }
}
