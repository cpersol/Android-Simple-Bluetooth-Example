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
    String brokerUrl = "ssl://gesinen.es:8882";
    String username = "gesinen";
    String password = "gesinen2110";
    String clientId = MqttClient.generateClientId();
    Button anotherServer;
    LinearLayout llContenido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt1);

        subText = findViewById(R.id.subText);
        anotherServer=findViewById(R.id.btnAnotherServer);
        llContenido = findViewById(R.id.llContenido);

       // String clientId = MqttClient.generateClientId();
      // client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.mqttdashboard.com:1883",clientId);
        //client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.43.41:1883",clientId);
        client = new MqttAndroidClient(this.getApplicationContext(), brokerUrl, clientId);
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
        }

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MQTTConnection.this,"connected!!",Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MQTTConnection.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
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

    }

    public void published(View v){
        //String topic = "topictesting";
        //String message = "HOLA SOY CARLOTA";
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
 //TODO: topin pra suscribirse debería ser fijo (borrar textview+tostring)PREG A BUCHU
    private void setSubscription(){
        //topicS=findViewById(R.id.topicSuscribe);
        //topicSuscribe= topicS.getText().toString();

        try{

            client.subscribe("carlaTestApp",0);


        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v){

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            IMqttToken token = client.connect(options);
            Log.d("connection",clientId);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MQTTConnection.this,"connected!!",Toast.LENGTH_LONG).show();
                    setSubscription();

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

   /* private TrustManager[] getTrustManagers() {
        try {
            // Cargar el certificado del servidor desde un archivo (por ejemplo, en formato PEM)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInputStream = getResources().openRawResource(R.raw.server_certificate); // Coloca el certificado en la carpeta "res/raw"
            Certificate cert = cf.generateCertificate(certInputStream);

            // Crear un almacén de claves y agregar el certificado
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("server", cert);

            // Crear un administrador de confianza y agregar el almacén de claves
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            return tmf.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
   private TrustManager[] getTrustManagers() {
       try {
           // Crear un administrador de confianza personalizado
           X509TrustManager trustManager = new X509TrustManager() {
               @Override
               public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                   // No es necesario implementar esta verificación en el lado del cliente
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