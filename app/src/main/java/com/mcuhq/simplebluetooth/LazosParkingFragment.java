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

public class LazosParkingFragment  extends Fragment {
    private Button buttonSetCurrent;
    private Button buttonSetReset;

    private Button buttonGetMaxValue;
    private Button buttonGetInfoLazos;
    private Button buttonSetMaxValue;
    private EditText txtC1setCurrent;
    private EditText txtC2setCurrent;
    private EditText txtC12setCurrent;
    private EditText txtC21setCurrent;
    private TextView txtGetMaxValue;


    public Headers headers = new Headers();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lazos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSetCurrent=view.findViewById(R.id.setCurrentButton) ;
        buttonSetReset=view.findViewById(R.id.setResetButton) ;
        buttonGetInfoLazos=view.findViewById(R.id.buttonGetInfoLazos) ;
        buttonGetMaxValue=view.findViewById(R.id.getMaxValueButton) ;
        buttonSetMaxValue=view.findViewById(R.id.setMaxvalueButton) ;

        txtC1setCurrent=view.findViewById(R.id.c1setCurrentText) ;
        txtC2setCurrent=view.findViewById(R.id.c2setCurrentText) ;
        txtC12setCurrent=view.findViewById(R.id.c12setCurrentText) ;
        txtC21setCurrent=view.findViewById(R.id.c21setCurrentText) ;
        txtGetMaxValue=view.findViewById(R.id.GetMaxValueView) ;




        buttonSetCurrent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String textC1 = String.valueOf(txtC1setCurrent.getText());
                String textC2 = String.valueOf(txtC2setCurrent.getText());
                String textC12 = String.valueOf(txtC12setCurrent.getText());
                String textC21 = String.valueOf(txtC21setCurrent.getText());
                String totalResult = textC1 + textC2 + textC12 + textC21;
                Log.d("buttonSetCurrent",totalResult);
            }
        });

        buttonGetInfoLazos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null){
                    String infoLazos =MainActivity.getInstance().getBluetoothMessage();
                    Log.d("infoLazos", "readMessage");
                    String [] partes = infoLazos.split("_");

                        String c1 = partes[2];
                        String c2 = partes[3];
                        String c12 = partes[4];
                        String c21 = partes[5];

                    txtC1setCurrent.setText(c1);
                    txtC2setCurrent.setText(c2);
                    txtC12setCurrent.setText(c12);
                    txtC21setCurrent.setText(c21);

                    }

            }
        });
        buttonGetMaxValue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null){
                    String infoLazos =MainActivity.getInstance().getBluetoothMessage();
                    Log.d("infoLazos", "readMessage");
                    String [] partes = infoLazos.split("_");

                    String getMaxValue = partes[6];

                    txtGetMaxValue.setText(getMaxValue);


                }

            }
        });
        buttonSetReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txtC1setCurrent.setText("");
                txtC2setCurrent.setText("");
                txtC12setCurrent.setText("");
                txtC21setCurrent.setText("");
            }
        });

    }
}
