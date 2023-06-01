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
                Intent i = new Intent(Menu.this, MenuGateway.class);
                startActivity(i);

            }
        });

       Plataforma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            irAPlataforma(AccessToken);

            }
        });

    }

 /*   private void irAPlataforma(String accessToken) {
        Uri uri = Uri.parse("https://test-platform.gesinen.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Bundle extras = new Bundle();
        try {
            JSONObject json = new JSONObject();
            json.put("accessToken", accessToken);
            String headerValue =  accessToken;
            extras.putString("x-access-token", headerValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtras(extras);
        startActivity(intent);
    }*/
 private void irAPlataforma(String accessToken) {
     Uri uri = Uri.parse("https://test-platform.gesinen.com");
     Intent intent = new Intent(Intent.ACTION_VIEW, uri);
     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     intent.setPackage("com.android.chrome"); // Cambia el nombre del paquete según el navegador que desees utilizar

     Bundle extras = new Bundle();
     try {
         JSONObject json = new JSONObject();
         json.put("accessToken", accessToken);
         String headerValue = accessToken;
         extras.putString("x-access-token", headerValue);
     } catch (JSONException e) {
         e.printStackTrace();
     }

     intent.putExtras(extras);

     if (intent.resolveActivity(getPackageManager()) != null) {
         startActivity(intent);
     } else {
         // No se encontró ninguna aplicación compatible, puedes mostrar un mensaje de error o manejarlo según tus necesidades
     }
 }
 /*private void irAPlataforma(String accessToken) {
     // Construye la URL de la plataforma
     String plataformaUrl = "https://test-platform.gesinen.com";

     // Agrega el AccessToken como parámetro en la URL
     Uri.Builder builder = Uri.parse(plataformaUrl).buildUpon();
     builder.appendQueryParameter("accessToken", accessToken);
     Uri plataformaUri = builder.build();

     // Abre la URL en un navegador externo
     Intent intent = new Intent(Intent.ACTION_VIEW, plataformaUri);
     startActivity(intent);
 }*/
}

