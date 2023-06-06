package com.mcuhq.simplebluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import java.util.Collections;

public class Menu extends Activity {
    private Button Sensores;
    private Button Gateway;
    private Button Plataforma;
    private String accessToken;
    private String authorizationHeader;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

         Sensores= (Button)findViewById(R.id.sensores);
         Gateway = (Button)findViewById(R.id.gateway);
         Plataforma = (Button)findViewById(R.id.plataforma);
         LoginActivity loginActivity= new LoginActivity();
         accessToken=loginActivity.accessToken;
         Log.d("accesToken_menu",accessToken);

        //authorizationHeader = "Bearer " + accessToken;
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
           // irAPlataforma(accessToken);


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
 /*private void irAPlataforma(String accessToken) {
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
 /*public void irAPlataforma() {


     // Crear una solicitud HTTP con la cabecera de autenticación
     Request request = new Request.Builder()
             .url("https://test-platform.gesinen.com")
             .addHeader("Authorization", authorizationHeader)
             .build();

     // Realizar la solicitud HTTP utilizando OkHttp
     OkHttpClient client = new OkHttpClient();
     Log.d("plataforma","plataforma");
     client.newCall(request).enqueue(new Callback() {

         @Override
         public void onResponse(Call call, Response response) {
             Log.d("plataforma","dentroPlataforma");
             String plataformaURL = response.request().url().toString();
             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(plataformaURL));
             startActivity(intent);
         }

         @Override
         public void onFailure(Call call, IOException e) {
             Log.d("plataforma","NOplataforma");
         }
     });
 }*/
/* private void irAPlataforma() {
     // Recuperar el access token de SharedPreferences
    // SharedPreferences sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE);
     // accessToken = sharedPreferences.getString("accessToken", null);

     if (accessToken != null) {
         String url = "https://test-platform.gesinen.com";
         Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.setPackage("com.android.chrome"); // Cambia el nombre del paquete según el navegador que desees utilizar

         // Agregar el access token al encabezado de la solicitud HTTP
         Map<String, String> headers = new HashMap<>();
         headers.put("Authorization", "Bearer " + accessToken);

         // Pasar los encabezados adicionales al navegador
         Bundle extras = new Bundle();
         extras.putSerializable("headers", (Serializable) headers);
         intent.putExtra(Browser.EXTRA_HEADERS, extras);
     
         if (intent.resolveActivity(getPackageManager()) != null) {
             startActivity(intent);
         } else {
             Log.d("entrada","no app compati");
             // No se encontró ninguna aplicación compatible, puedes mostrar un mensaje de error o manejarlo según tus necesidades
         }
     } else {
         Log.d("entrada","no token");
         // No se encontró el access token en SharedPreferences, tal vez el usuario no ha iniciado sesión previamente
         // Puedes mostrar un mensaje o redirigir a la pantalla de inicio de sesión
     }
 }*/
  /*  private void irAPlataforma() {
     // Recuperar el access token de SharedPreferences
     // SharedPreferences sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE);
     // accessToken = sharedPreferences.getString("accessToken", null);

     if (accessToken != null) {
         String url = "https://test-platform.gesinen.com?access_token=" + accessToken;
         Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.setPackage("com.android.chrome"); // Cambia el nombre del paquete según el navegador que desees utilizar

         if (intent.resolveActivity(getPackageManager()) != null) {
             startActivity(intent);
         } else {
             Log.d("entrada","no app compati");
             // No se encontró ninguna aplicación compatible, puedes mostrar un mensaje de error o manejarlo según tus necesidades
         }
     } else {
         Log.d("entrada","no token");
         // No se encontró el access token en SharedPreferences, tal vez el usuario no ha iniciado sesión previamente
         // Puedes mostrar un mensaje o redirigir a la pantalla de inicio de sesión
     }
 }*/
    private void irAPlataforma (String accestoken){
        Log.d("accestokenPLAT",accestoken);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Configura los encabezados de la solicitud con el token JWT
       // String token = "tu_token_jwt";
        String url = "https://test-platform.gesinen.com";
        String headers = "Authorization: Bearer " + accestoken;
        webView.loadUrl(url, Collections.singletonMap("Authorization", headers));

    }
}

