package com.mcuhq.simplebluetooth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class GatewayActivity extends AppCompatActivity {
    private EditText ipField;
    private EditText commandField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText portField;
    private TextView shellOutput_ssh;

    private String command_ssh;
    private Button crearConexion;
    private Button enviarComando;
    private Button decoder5000;
    private Button chirpstack8080;
    private SshClient client;
    private String ip;
    private String username;
    private String password;
    private String command;
    private int port;
    private String portText;
    private ClientChannel channel;
    private boolean conectado=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);


        // Declare fields
        ipField = (EditText) findViewById(R.id.IpField);
        usernameField = (EditText) findViewById(R.id.UsernameField);
        passwordField = (EditText) findViewById(R.id.PasswordField);
        portField= (EditText) findViewById(R.id.PortField);
        commandField = (EditText) findViewById(R.id.CommandField);
        shellOutput_ssh = findViewById(R.id.sshTextView);
        crearConexion= findViewById(R.id.ButtonCrearConexion);
        enviarComando= findViewById(R.id.ButtonComando);
        decoder5000=findViewById(R.id.decoder5000Button);
        chirpstack8080=findViewById(R.id.chirpstack8080Button);
        // Get input data from fields


        crearConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = ipField.getText().toString();
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();
                command = commandField.getText().toString();
                portText=portField.getText().toString();

                try{
                    port=Integer.parseInt(portText);
                }catch (NumberFormatException e) {
                    // Manejar la excepción aquí
                }

                authenticate();

            }
        });

        enviarComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnviarComando(v);
            }
        });

        decoder5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               redireccionar5000(v);
            }
        });

        chirpstack8080.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             redireccionar8080(v);
            }
        });
    }

    public void authenticate() {

        command_ssh = "pwd\n";

        // Setting user.com property manually
        // since isn't set by default in android
        String key = "user.home";
        String val = getApplicationContext().getApplicationInfo().dataDir;
        System.setProperty(key, val);

        // Creating a client instance
         client = SshClient.setUpDefaultClient();
        client.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        client.start();
        Log.d("AUTHENTICATE","PARAMETROS");
        CrearConexión();
    }


    public void CrearConexión (){
       // if (!portText.isEmpty()) {
            Log.d("CREAR-SSH","port");
       // port=Integer.parseInt(portText);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("CREAR-SSH","run");
                    // Connection establishment and authentication
                        try (ClientSession session = client.connect(username, ip ,port).verify(10000).getSession()) {
                            Log.d("CREAR-SSH","verificado");
                        session.addPasswordIdentity(password);
                        session.auth().verify(50000);
                        System.out.println("Connection establihed");

                        // Create a channel to communicate
                        channel = session.createChannel(Channel.CHANNEL_SHELL);
                        System.out.println("Starting shell");

                        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                        channel.setOut(responseStream);

                        // Open channel
                        channel.open().verify(5, TimeUnit.SECONDS);
                        try (OutputStream pipedIn = channel.getInvertedIn()) {
                            pipedIn.write(command_ssh.getBytes());
                            pipedIn.flush();
                        }

                        // Close channel
                        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                                TimeUnit.SECONDS.toMillis(5));

                        // Output after converting to string type
                        String responseString = new String(responseStream.toByteArray());
                        System.out.println(responseString);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                shellOutput_ssh.setText(responseString);
                            }

                        });
                            Log.d("CREAR-SSH","CREADA");
                        conectado=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        client.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
                    thread.start();
   // }
    }

    public void setEnviarComando(View view) {
        if (conectado){
        try {
            OutputStream pipedIn = channel.getInvertedIn();
            pipedIn.write(command.getBytes());
            pipedIn.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}


    public void redireccionar5000(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://" + ip + ":5000"));
        startActivity(intent);
    }

    public void redireccionar8080(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://" + ip + ":8080"));
        startActivity(intent);
    }
}







