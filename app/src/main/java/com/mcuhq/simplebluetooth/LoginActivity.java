package com.mcuhq.simplebluetooth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    public static String accessToken;
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
                hacerLogin(username,password);
                Log.d("login",username);
                Log.d("login",password);

            }
        });
    }

    private void hacerLogin(String email, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://82.223.152.25:3000/api/V1_1/auth/signin";
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
                        //accessToken = response.getString("accessToken");
                        String tokens = response.toString();
                        JSONObject tokensJson = new JSONObject(tokens);
                        accessToken = tokensJson.getString("accessToken");
                        // Guardar los tokens en SharedPreferences después del inicio de sesión
                        SharedPreferences sharedPreferences = getSharedPreferences("Tokens", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("accessToken",accessToken );
                        Log.d("accessToken", accessToken);
                       // editor.putString("refreshToken", "tu_refresh_token");
                        editor.apply();


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