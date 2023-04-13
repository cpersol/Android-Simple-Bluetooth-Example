package com.mcuhq.simplebluetooth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class Menu extends Activity {
    private Button Sensores;
    private Button Gateway;
    private Button Plataforma;
    private String AccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

         Sensores= (Button)findViewById(R.id.sensores);
         Gateway = (Button)findViewById(R.id.gateway);
         Plataforma = (Button)findViewById(R.id.plataforma);
         LoginActivity loginActivity= new LoginActivity();
         AccessToken=loginActivity.accessToken;

        Sensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, MainActivity.class);
                startActivity(i);

            }
        });

        Gateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

       Plataforma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           // irAPlataforma(AccessToken);

            }
        });

    }

    private void irAPlataforma(String accessToken) {
        Uri uri = Uri.parse("https://swat-id.gesinen.com/#/sessions/signin");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Bundle extras = new Bundle();
        try {
            JSONObject json = new JSONObject();
            json.put("accessToken", accessToken);
            String headerValue =  json.toString();
            extras.putString("x-access-token", headerValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtras(extras);
        startActivity(intent);
    }

}
