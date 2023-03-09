package com.mcuhq.simplebluetooth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GenericFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenericFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BluetoothConnection mConnectionBT;
    private String readMessage = null;
    private Button mButtonTerminal;
    private TextView mTerminal;
    private String informacionRecibida;
    public GenericFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
   /* public static GenericFragment newInstance(String param1, String param2) {
        GenericFragment fragment = new GenericFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_generic, container, false);
        mConnectionBT = new BluetoothConnection(getActivity());

        //mHandler= new HandleBluetooth(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (readMessage == null) {
            Log.d("msg", "readMessage null");
        }
        informacionRecibida=mConnectionBT.lastMessage;
        mTerminal=view.findViewById(R.id.terminalView) ;
      //  mTerminal.setMovementMethod(new ScrollingMovementMethod());
       // mTerminal.setScrollbarFadingEnabled(false);
        mButtonTerminal=view.findViewById(R.id.terminalButton) ;


        mButtonTerminal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mensaje = "Información recibida: " + informacionRecibida;
                mTerminal.append(mensaje + "\n");

            }
        });

    }
}