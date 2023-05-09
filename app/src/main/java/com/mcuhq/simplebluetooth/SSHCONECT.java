package com.mcuhq.simplebluetooth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class SSHCONECT extends AppCompatActivity {

    private static JSch jsch;

    static EditText usuario;
    static EditText hostname;
    static EditText pass;
    public Toast t;
    static EditText commandText;
    static String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gateway);

        usuario = (EditText) findViewById(R.id.UsernameField);

        pass = (EditText) findViewById(R.id.PasswordField);

        hostname = (EditText) findViewById(R.id.IpField);

        commandText= (EditText)findViewById(R.id.CommandField);

        final String username = usuario.getText().toString();

        final String password = pass.getText().toString();

        final String ip = hostname.getText().toString();


        Button crearConexion= (Button) findViewById(R.id.ButtonCrearConexion);
        Button enviarComando= (Button) findViewById(R.id.ButtonComando);




        crearConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*EJECUTAR EN SEGUNDO PLANO*/
                new AsyncTask<String, Void, Void>() {

                    @Override
                    protected Void doInBackground(String... params) {
                        try {

                            String username = params[0];
                            String password = params[1];
                            String ip = params[2];

                            String r = executeRemoteCommand(username, password, ip, 22);
                            Log.d("return",r);

                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                        return null;
                    }
                }.execute(username, password, ip);

            }
        });


        enviarComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, Void>() {

                    @Override
                    protected Void doInBackground(String... params) {
                        try {

                            String username = params[0];
                            String password = params[1];
                            String ip = params[2];

                            String r = executeRemoteCommand(username, password, ip, 22);
                            Log.d("return",r);

                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                        return null;
                    }
                }.execute(username, password, ip);

            }
        });

    }

    /*METODO EJECUTAR COMANDO*/
    public static String executeRemoteCommand(String username, String password, String ip,
                                              int port)
            throws Exception {

        command=commandText.getText().toString();

            username = usuario.getText().toString();

            password = pass.getText().toString();

            ip = hostname.getText().toString();

            port = 22;

            jsch = new JSch();

            Session sesion = jsch.getSession(username, ip, port);
            Log.d("SESION", "Session Get");
            sesion.setPassword(password);
            Log.d("SESION", "Session pass");

            /*PREGUNTAR POR INTERCAMBIO ESTRICTO DE LLAVES*/

            Properties prop = new Properties();

            prop.put("StrictHostKeyChecking", "no");

            sesion.setConfig(prop);
            Log.d("SESION", "Session setConfig");

            /*CONECTAMOS LA SESION*/

            sesion.connect();

            /*CONFIGURACION DEL CANAL SSH*/
            Channel canalssh = sesion.openChannel("exec");

            // ChannelExec canalssh = new ChannelExec();

            // sesion.openChannel("exec");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            /*EJECUTAMOS EL COMANDO*/
            command=commandText.getText().toString();

            ((ChannelExec) canalssh).setCommand((command != "")?command:"pwd");

            Log.d("COMMAND", "comando");

            canalssh.setOutputStream(baos);

            canalssh.connect(5000);

            // canalssh.disconnect();
            // canalssh.getOutputStream();
            while (!canalssh.isClosed()) {
                Thread.sleep(1000);
            }

            canalssh.disconnect();
            return baos.toString();
        }

    }