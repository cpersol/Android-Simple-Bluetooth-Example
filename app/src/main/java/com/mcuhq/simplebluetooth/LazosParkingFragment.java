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
    private Button buttonGetCurrent;
    private Button buttonGetMaxValue;


    private EditText txtC1setCurrent;
    private EditText txtC2setCurrent;
    private EditText txtC12setCurrent;
    private EditText txtC21setCurrent;
    private EditText txtC1setReset;
    private EditText txtC2setReset;
    private EditText txtC12setReset;
    private EditText txtC21setReset;
    private EditText txtC1getCurrent;
    private EditText txtC2getCurrent;
    private EditText txtC12getCurrent;
    private EditText txtC21getCurrent;
    private EditText txtC1getMaxValue;
    private EditText txtC2getMaxValue;
    private EditText txtC12getMaxValue;
    private EditText txtC21getMaxValue;

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
        buttonGetCurrent=view.findViewById(R.id.getCurrentButton) ;
        buttonGetMaxValue=view.findViewById(R.id.getMaxvalueButton) ;

        txtC1setCurrent=view.findViewById(R.id.c1setCurrentText) ;
        txtC2setCurrent=view.findViewById(R.id.c2setCurrentText) ;
        txtC12setCurrent=view.findViewById(R.id.c12setCurrentText) ;
        txtC21setCurrent=view.findViewById(R.id.c21setCurrentText) ;

        txtC1setReset=view.findViewById(R.id.c1setResetText) ;
        txtC2setReset=view.findViewById(R.id.c2setResetText) ;
        txtC12setReset=view.findViewById(R.id.c12setResetText) ;
        txtC21setReset=view.findViewById(R.id.c21setResetText) ;

        txtC1getCurrent=view.findViewById(R.id.c1getCurrentText) ;
        txtC2getCurrent=view.findViewById(R.id.c2getCurrentText) ;
        txtC12getCurrent=view.findViewById(R.id.c12getCurrentText) ;
        txtC21getCurrent=view.findViewById(R.id.c21getCurrentText) ;

        txtC1getMaxValue=view.findViewById(R.id.c1getMaxValueText) ;
        txtC2getMaxValue=view.findViewById(R.id.c2getMaxValueText) ;
        txtC12getMaxValue=view.findViewById(R.id.c12getMaxValueText) ;
        txtC21getMaxValue=view.findViewById(R.id.c21getMaxValueText) ;


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
        buttonSetReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        buttonGetCurrent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        buttonGetMaxValue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });


    }
}
