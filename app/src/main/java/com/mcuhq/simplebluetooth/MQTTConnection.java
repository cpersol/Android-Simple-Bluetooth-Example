package com.mcuhq.simplebluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MQTTConnection extends AppCompatActivity {

    MqttAndroidClient client;
    TextView subText;
    EditText topicS;
    EditText topicP;
    EditText msg;
    String topicSuscribe;
    String topicPublish;
    String mensaje;
    String brokerUrl ;
    String username ;
    String password ;
    String clientId = MqttClient.generateClientId();
    Button anotherServer;
    LinearLayout llContenido;
    EditText newBrokerPort;
    EditText newUsername;
    EditText newPassword;
    Button anotherConnection;
    Button connect;
    String NewBrokerPort;
    String NewUsername;
    String NewPassword;
    Button suscribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt1);

        subText = findViewById(R.id.subText);
        anotherServer=findViewById(R.id.btnAnotherServer);
        llContenido = findViewById(R.id.llContenido);
        newBrokerPort = findViewById(R.id.newBroker);
        newUsername = findViewById(R.id.newUsername);
        newPassword = findViewById(R.id.newPassword);
        anotherConnection = findViewById(R.id.connectAnotherServer);
        connect = findViewById(R.id.connBtn);
        topicS=findViewById(R.id.topicSuscribe);
        suscribe=findViewById(R.id.btnSuscribe);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username="gesinen";
                password="gesinen2110";
                brokerUrl="ssl://gesinen.es:8882";
                client = new MqttAndroidClient(MQTTConnection.this, brokerUrl, clientId);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(username);
                options.setPassword(password.toCharArray());
                // Configuración de SSL/TLS
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, getTrustManagers(), null);
                    SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                    options.setSocketFactory(socketFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                    }
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        subText.setText(new String(message.getPayload()));
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
                conn(v,options);
            }
        });
        anotherConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 NewBrokerPort=newBrokerPort.getText().toString();
                 NewUsername=newUsername.getText().toString();
                 NewPassword=newPassword.getText().toString();
                client = new MqttAndroidClient(MQTTConnection.this, NewBrokerPort, clientId);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(NewUsername);
                options.setPassword(NewPassword.toCharArray());
                conn(v,options);
                // Configuración de SSL/TLS
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, getTrustManagers(), null);
                    SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                    options.setSocketFactory(socketFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                    }
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        subText.setText(new String(message.getPayload()));
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
                conn(v,options);
            }
        });

        anotherServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llContenido.getVisibility() == View.VISIBLE) {
                    llContenido.setVisibility(View.GONE);
                } else {
                    llContenido.setVisibility(View.VISIBLE);
                }
            }
        });
        suscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicSuscribe= topicS.getText().toString();
                setSubscription(topicSuscribe);
                Log.d("suscribe",topicSuscribe);
            }
        });

    }

    public void published(View v){
        topicP=findViewById(R.id.topicPublish);
        topicPublish=topicP.getText().toString();
        msg= findViewById(R.id.msg);
        mensaje=msg.getText().toString();

        try {
            client.publish(topicPublish, mensaje.getBytes(),0,false);


            Toast.makeText(this,"Published Message",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription(String topicSubscribe){


        try{

            client.subscribe(topicSubscribe,0);


        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v,MqttConnectOptions options){

        try {
            IMqttToken token = client.connect(options);
            Log.d("connection",clientId);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MQTTConnection.this,"connected!!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MQTTConnection.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    public void disconn(View v){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MQTTConnection.this,"Disconnected!!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MQTTConnection.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

   private TrustManager[] getTrustManagers() {
       try {
           X509TrustManager trustManager = new X509TrustManager() {
               @Override
               public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
               }

               @Override
               public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                   // Aquí puedes realizar la validación personalizada del certificado del servidor si es necesario
                   // Por ejemplo, puedes verificar la cadena de certificados o realizar otras comprobaciones

                   // Si confías en el certificado del servidor sin ninguna validación adicional, puedes omitir esta implementación
               }

               @Override
               public X509Certificate[] getAcceptedIssuers() {
                   return new X509Certificate[0]; // No es necesario para este caso
               }
           };

           return new TrustManager[] { trustManager };
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }

}