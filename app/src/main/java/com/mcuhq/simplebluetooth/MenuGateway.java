package com.mcuhq.simplebluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuGateway extends Activity {
    private Button GatewayConfig;
    private Button Mqtt;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_menu_gateway);

        GatewayConfig=(Button)findViewById(R.id.GatewayConfigButton);
        Mqtt=(Button)findViewById(R.id.MqttButton);


        GatewayConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuGateway.this, GatewayActivity.class);
                startActivity(i);

            }
        });


        Mqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuGateway.this, MQTTActivity.class);
                startActivity(i);

            }
        });
    }
}