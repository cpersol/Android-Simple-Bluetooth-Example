package com.mcuhq.simplebluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {
    private Button Sensores;
    private Button Gateway;
    private Button Plataforma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

       Sensores= (Button)findViewById(R.id.sensores);
        Gateway = (Button)findViewById(R.id.gateway);
        Plataforma = (Button)findViewById(R.id.plataforma);


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


            }
        });

    }



}
