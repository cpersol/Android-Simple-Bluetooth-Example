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


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private EditText Username;
    private EditText Password;
    private Button LoginButton;
    public String accessToken;
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

               /* if (username.equals("admin") && password.equals("admin")) {
                    Log.d("inicio","admin");
                    Intent i = new Intent(LoginActivity.this, Menu.class);
                    startActivity(i);
                } else {

                    Toast.makeText(getApplicationContext(),getString(R.string.sBTdevNF),Toast.LENGTH_SHORT).show();
                }*/

                hacerLogin(username,password);
                Log.d("login",username);
                Log.d("login",password);

            }
        });
    }

    private void hacerLogin(String email, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.137.1:3000/api/V1_1/auth/signin";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", email);
            jsonBody.put("pswd", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try{
                        Log.d("accessToken", String.valueOf(response));

                    if (response.has("accessToken")) {
                        accessToken = response.getString("accessToken");
                        Log.d("accessToken","accessToken");
                        Intent i = new Intent(LoginActivity.this, Menu.class);
                        startActivity(i);
                        // Guardar el accessToken en SharedPreferences o en una variable global
                        // Pasar a la siguiente actividad (la que contiene los botones)
                    } else {
                        // Si la respuesta no es 200 OK, mostrar un mensaje de error al usuario
                    }}
                    catch  (JSONException e) {
                        // manejar la excepción de análisis JSON
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Si ocurre un error al hacer la llamada, mostrar un mensaje de error al usuario
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}