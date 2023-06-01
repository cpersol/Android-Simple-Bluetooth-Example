package com.mcuhq.simplebluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuGateway extends Activity {
    private Button Ssh;
    private Button Mqtt;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_menu_gateway);

        Ssh=(Button)findViewById(R.id.SshButton);
        Mqtt=(Button)findViewById(R.id.MqttButton);


        Ssh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuGateway.this, SSHCONECT.class);
                startActivity(i);

            }
        });


        Mqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuGateway.this, MQTTConnection.class);
                startActivity(i);

            }
        });
    }
}