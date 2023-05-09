package com.mcuhq.simplebluetooth;

import android.content.Intent;
import android.os.Bundle;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class sshActivity extends AppCompatActivity {
    ClientChannel channel;
    TextView shellOutput;
    String host, username, password;
    Integer port;
    String command;
    Button enviarComando;
    EditText comandoAEnviar;
    Button desconectarSSH;
    SSHConnection sshConnection;
    ClientSession session ;
    SshClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssh2);

        // set output field
        shellOutput = findViewById(R.id.textView);
        enviarComando = findViewById(R.id.enviarCommandButton);
        comandoAEnviar = findViewById(R.id.commandEditText);
        desconectarSSH = findViewById(R.id.disconnectSSH);

        // Get user credentials from intent
        Intent intent = getIntent();
        host = intent.getStringExtra("host");
        port = Integer.parseInt(intent.getStringExtra("port"));
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        // Command which will be executed
        command = "pwd\n";

        // Setting user.com property manually
        // since isn't set by default in android
        String key = "user.home";
        String val = getApplicationContext().getApplicationInfo().dataDir;
        System.setProperty(key, val);

        // Creating a client instance
        client = SshClient.setUpDefaultClient();
        client.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        client.start();

        try {
            // Connection establishment and authentication
            session = client.connect(username, host, port).verify(10000).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(50000);
            Log.d("SSH Connection", "Connection established");

            // Create a channel to communicate
            channel = session.createChannel(Channel.CHANNEL_SHELL);
            Log.d("SSH Channel", "Channel created");

            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOut(responseStream);

            // Open channel
            channel.open().verify(5, TimeUnit.SECONDS);
            Log.d("SSH Channel", "Channel opened");

            // Implement a callback or listener to notify the main thread when the SSH connection has been established and the channel is ready to use.
            channel.waitFor(EnumSet.of(ClientChannelEvent.OPENED), TimeUnit.SECONDS.toMillis(5));
            Log.d("SSH Channel", "Channel ready to use");

            enviarComando.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String command = comandoAEnviar.getText().toString() + "\n";
                    OutputStream pipedIn = channel.getInvertedIn();
                    try {
                        pipedIn.write(command.getBytes());
                        pipedIn.flush();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    InputStream responseStream = channel.getInvertedOut();
                    byte[] responseBytes = new byte[1024];
                    int numRead = 0;
                    try {
                        numRead = responseStream.read(responseBytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String responseString = new String(responseBytes, 0, numRead);
                    shellOutput.setText(responseString);

                }});
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SSH Connection", "Error establishing connection");
        }
    }
}
