package com.mcuhq.simplebluetooth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mcuhq.simplebluetooth.ProcessToSendMessage;


public class GenericFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button buttonTerminal;
    private Button buttonAppKey;
    private Button buttonDeveui;
    private Button buttonTimeTx;
    private Button buttonInput;
    private Button buttonGetConfigSensor;
    private EditText appKeyText;
    private EditText deveuiText;
    private EditText timeTxText;
    private EditText inputText;
    private String appkey_hexString;
    private String appkey_base64String;
    private String deveui_hexString;
    private String deveui_base64String;
    private String interval_hexString;
    private String interval_base64String;
    public Headers headers = new Headers();



    private TextView mTerminal;
    private String informacionRecibida;
    private MainActivity mainActivity;
    public  String readMessage;
    private TextView gBluetoothStatus;
    private TextView gReadBuffer;
    public GenericFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_generic, container, false);
        //MainActivity.getInstance().mConnectionBT.setActivityInMainActivity(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTerminal=view.findViewById(R.id.terminalView) ;
        buttonTerminal=view.findViewById(R.id.terminalButton) ;
        buttonAppKey=view.findViewById(R.id.appKeyButton) ;
        buttonDeveui=view.findViewById(R.id.deveuiButton) ;
        buttonTimeTx=view.findViewById(R.id.txButton) ;
        buttonInput=view.findViewById(R.id.inputButton) ;
        appKeyText= view.findViewById(R.id.txtapp_key);
        deveuiText= view.findViewById(R.id.txtdev_eui);
        timeTxText= view.findViewById(R.id.txttime_tx);
        inputText= view.findViewById(R.id.txtInputTerminal);
        buttonGetConfigSensor= view.findViewById(R.id.getInfoButton);


        buttonTerminal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                readMessage=MainActivity.getInstance().getBluetoothMessage();
                String mensaje = "Información recibida: " + readMessage;
                mTerminal.append(mensaje + "\n");
            }
        });
        buttonAppKey.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null) //First check to make sure thread created
                    appkey_hexString = Headers.SET_APPKEY+appKeyText.getText().toString();
                    String appkey_base64String=ProcessToSendMessage.hexToBase64(appkey_hexString);
                   MainActivity.getInstance().mConnectionBT.mConnectedThread.write(appkey_base64String);
            }
        });
        buttonDeveui.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null) //First check to make sure thread created
                    deveui_hexString = Headers.SET_DEVEUI+deveuiText.getText().toString();
                     String deveui_base64String=ProcessToSendMessage.hexToBase64(deveui_hexString);
                    MainActivity.getInstance().mConnectionBT.mConnectedThread.write( deveui_base64String);

            }
        });
        buttonTimeTx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null) //First check to make sure thread created
                    interval_hexString = Headers.SET_TIME+timeTxText.getText().toString();
                    String interval_base64String=ProcessToSendMessage.hexToBase64(interval_hexString);
                    MainActivity.getInstance().mConnectionBT.mConnectedThread.write(interval_base64String);

            }
        });
        buttonInput.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null) //First check to make sure thread created
                    MainActivity.getInstance().mConnectionBT.mConnectedThread.write(inputText.getText().toString());

            }
        });
        buttonGetConfigSensor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String infoSensor =MainActivity.getInstance().getBluetoothMessage();
                Log.d("infoSensor", "readMessage");
                String [] partes = infoSensor.split("_");


                String header = partes[0];

                if(header.equals(headers.GET_GENERIC_INFO)) {
                    Log.d("infoSensor", "Split");
                    String appKey = partes[1];
                    String deveui = partes[2];
                    String time = partes[3];

                    appKeyText.setText(appKey);
                    deveuiText.setText(deveui);
                    timeTxText.setText(time);

                }}
        });

    }
}