package com.mcuhq.simplebluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.constraint.ConstraintLayout;


public class LoginActivity extends AppCompatActivity {
    private EditText Username;
    private EditText Password;
    private Button LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username = (EditText) findViewById(R.id.editTextEmail);
        Password =(EditText) findViewById(R.id.editTextPassword);
        LoginButton = (Button)findViewById(R.id.buttonLogin);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();

                if (username.equals("admin") && password.equals("admin")) {
                    Log.d("inicio","admin");
                    Intent i = new Intent(LoginActivity.this, Menu.class);
                    startActivity(i);
                } else {

                    Toast.makeText(getApplicationContext(),getString(R.string.sBTdevNF),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}