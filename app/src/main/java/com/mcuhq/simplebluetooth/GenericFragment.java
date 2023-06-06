package com.mcuhq.simplebluetooth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class GenericFragment extends Fragment {
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
    private String deveui_hexString;
    private String interval_intString;
    public Headers headers = new Headers();
    private String input_hexString;
    private TextView textViewAPPKEY;
    private TextView textViewDEVEUI;
    private TextView textViewINTERVAL;
    private TextView textViewINPUT;
    private TextView mTerminal;
    public String readMessage;


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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTerminal = view.findViewById(R.id.terminalView);
        buttonTerminal = view.findViewById(R.id.terminalButton);
        buttonAppKey = view.findViewById(R.id.appKeyButton);
        buttonDeveui = view.findViewById(R.id.deveuiButton);
        buttonTimeTx = view.findViewById(R.id.txButton);
        buttonInput = view.findViewById(R.id.inputButton);
        appKeyText = view.findViewById(R.id.txtapp_key);
        deveuiText = view.findViewById(R.id.txtdev_eui);
        timeTxText = view.findViewById(R.id.txttime_tx);
        inputText = view.findViewById(R.id.txtInputTerminal);
        buttonGetConfigSensor = view.findViewById(R.id.getInfoButton);
        textViewAPPKEY = view.findViewById(R.id.textViewAppKey);
        textViewDEVEUI = view.findViewById(R.id.textViewDeveui);
        textViewINTERVAL = view.findViewById(R.id.textViewInterval);
        textViewINPUT = view.findViewById(R.id.textViewInput);
        buttonTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMessage = MainActivity.getInstance().getBluetoothMessage();
                String mensaje = "Información recibida: " + readMessage;
                mTerminal.append(mensaje + "\n");
            }
        });
        buttonAppKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getInstance().mConnectionBT.mConnectedThread != null) {//First check to make sure thread created
                    String appKeyEditText=appKeyText.getText().toString();
                    appkey_hexString = Headers.SET_APPKEY + appKeyEditText;
                    if (!appKeyEditText.isEmpty()){
                    if (ProcessToSendMessage.isHexadecimal(appkey_hexString)) {
                        String appkey_base64String = ProcessToSendMessage.hexToBase64(appkey_hexString);
                        MainActivity.getInstance().mConnectionBT.mConnectedThread.write(appkey_base64String);
                        appKeyText.setText("");
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sent), Toast.LENGTH_SHORT).show();
                    } else {
                        textViewAPPKEY.setText(MainActivity.getInstance().mConnectionBT.errorTextHexFormat);

                    }

                }}
            }
        });
        buttonDeveui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getInstance().mConnectionBT.mConnectedThread != null) {//First check to make sure thread created
                   String deveuiEditText=deveuiText.getText().toString();
                    deveui_hexString = Headers.SET_DEVEUI + deveuiEditText;
                if (!deveuiEditText.isEmpty()){
                if (ProcessToSendMessage.isHexadecimal(deveui_hexString)) {
                    String deveui_base64String = ProcessToSendMessage.hexToBase64(deveui_hexString);
                    MainActivity.getInstance().mConnectionBT.mConnectedThread.write(deveui_base64String);
                    deveuiText.setText("");
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sent), Toast.LENGTH_SHORT).show();
                } else {
                    textViewDEVEUI.setText(MainActivity.getInstance().mConnectionBT.errorTextHexFormat);

                }

            }}}
        });
        buttonTimeTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getInstance().mConnectionBT.mConnectedThread != null){//First check to make sure thread created
                    String timeTxEditText=timeTxText.getText().toString();
                    interval_intString = Headers.SET_TIME + timeTxEditText;
                    if(!timeTxEditText.isEmpty()){
                MainActivity.getInstance().mConnectionBT.mConnectedThread.write(interval_intString);
                timeTxText.setText("");
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sent), Toast.LENGTH_SHORT).show();
            }}}
        });

        buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getInstance().mConnectionBT.mConnectedThread != null)//First check to make sure thread created
                    input_hexString = inputText.getText().toString();
                if(!input_hexString.isEmpty()){
                if (ProcessToSendMessage.isHexadecimal(input_hexString)) {
                    String input_base64String = ProcessToSendMessage.hexToBase64(input_hexString);
                    MainActivity.getInstance().mConnectionBT.mConnectedThread.write(input_base64String);
                    textViewINPUT.setText("");
                } else {
                    textViewINPUT.setText(MainActivity.getInstance().mConnectionBT.errorTextHexFormat);
                }

            }}
        });
        buttonGetConfigSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getInstance().mConnectionBT.mConnectedThread != null) {
                    String infoSensor = MainActivity.getInstance().getBluetoothMessage();
                    Log.d("infoSensor", "readMessage");
                    String[] partes = infoSensor.split("_");


                    String header = partes[0];

                    if (header.equals(headers.GET_GENERIC_INFO)) {
                        Log.d("infoSensor", "Split");
                        String appKey = partes[1];
                        String deveui = partes[2];
                        String time = partes[3];

                        appKeyText.setText(appKey);
                        deveuiText.setText(deveui);
                        timeTxText.setText(time);

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sBTdevNF), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
