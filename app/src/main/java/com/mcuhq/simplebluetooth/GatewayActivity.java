package com.mcuhq.simplebluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class GatewayActivity extends AppCompatActivity {
    private EditText hostField;
    private EditText portField;
    private EditText usernameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
    }

    public void authenticate(View view) {

        // Create an intent for sshActivity
        Intent intent = new Intent(this, sshActivity.class);

        // Declare fields
         hostField = (EditText) findViewById(R.id.hostField);
         portField = (EditText) findViewById(R.id.portField);
         usernameField = (EditText) findViewById(R.id.usernameField);
         passwordField = (EditText) findViewById(R.id.passwordField);

        // Get input data from fields
        String host = hostField.getText().toString();
        String port = portField.getText().toString();
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        // Pass on data to sshActivity via intent
        intent.putExtra("host", host);
        intent.putExtra("port", port);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }






}
