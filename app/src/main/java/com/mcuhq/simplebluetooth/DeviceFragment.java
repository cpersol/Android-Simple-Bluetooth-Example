 /*package com.mcuhq.simplebluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;



public class DeviceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
     private final static String TYPE_IBOX = "IBOX";
     private final static String TYPE_STA = "STA";
     private final static String TYPE_BOILER = "BOILER";
    private final static String TYPE_LAZOSPARKING= "LAZOSPARKING";
    private String infoType;


     // TODO: Rename and change types of parameters
     private String mParam1;
     private String mParam2;
     private final String TAG = MainActivity.class.getSimpleName();

     private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

     // #defines for identifying shared types between calling functions
     private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
     public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
     private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
     private TabLayout tabLayout;
     public static final int RESULT_OK = -1;
    public Headers headers = new Headers();
    public String infoGeneric;
     private String readMessage = null;

     // GUI Components
     private TextView mBluetoothStatus;
     private TextView mReadBuffer;
     private String type;
     private Button mOffBtn;
     private Button mListPairedDevicesBtn;
     private Button mDiscoverBtn;
     private ListView mDevicesListView;
     private CheckBox mLED1;
     private Button ButtonGetInfo;
     private BluetoothAdapter mBTAdapter;
     private Set<BluetoothDevice> mPairedDevices;
     private ArrayAdapter<String> mBTArrayAdapter;

     private Handler mHandler; // Our main handler that will receive callback notifications
     private ConnectedThread dConnectedThread; // bluetooth background worker thread to send and receive data
     private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    // TODO: Rename and change types of parameters

     private BluetoothConnection dConnectionBT;
     private Button ButtonType;
     private Activity activity;

     @Override  //https://es.stackoverflow.com/questions/204586/como-enviar-datos-de-un-fragment-a-otro-fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_device, container, false);
        dConnectionBT = MainActivity.getInstance().mConnectionBT;
        //mHandler= new HandleBluetooth(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(readMessage == null){
            Log.d("msg", "readMessage null");
        }
        ButtonType=view.findViewById(R.id.buttonType);



        mBTArrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio



        // Ask for location permission if not already allowed
     //   if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
       //     ActivityCompat.requestPermissions(new _BluetoothActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        MainActivity.getInstance().mConnectionBT.setupHandler();

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
//            mBluetoothStatus.setText(getString(R.string.sBTstaNF));
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.sBTdevNF),Toast.LENGTH_SHORT).show();
        }
        else {

            ButtonType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        cambiarFragmentoEnBaseAVariable();
                    }
            });


        }}




                 public void cambiarFragmentoEnBaseAVariable() {
                     if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null){
                         Log.d("infoSensorDev", "readMessage");
                         infoType= MainActivity.getInstance().getBluetoothMessage();
                         String [] partes = infoType.split("_");

                         String header = partes[0];
                         type = partes[1];
                         if(header.equals(headers.GET_DEVICE_INFO)) {
                             Log.d("infoSensorDev", type);

                             if (type.contains(TYPE_IBOX)) {
                                 Log.d("infoSensorDev", "PATATA");

                                 FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                 IboxFragment iboxFragment = new IboxFragment();
                                 FragmentTransaction transaction = fragmentManager.beginTransaction();
                                 transaction.replace(R.id.fragmentDev, iboxFragment);
                                 //  transaction.addToBackStack(null);
                                 transaction.commit();
                             }
                                 if (type.contains(TYPE_STA)){
                                     FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                     StaFragment staFragment = new StaFragment();
                                     FragmentTransaction transaction = fragmentManager.beginTransaction();
                                     transaction.replace(R.id.fragmentDev, staFragment);
                                     //  transaction.addToBackStack(null);
                                     transaction.commit();
                                 }
                                     if (type.contains(TYPE_BOILER)) {
                                         FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                         BoilerFragment boilerFragment = new BoilerFragment();
                                         FragmentTransaction transaction = fragmentManager.beginTransaction();
                                         transaction.replace(R.id.fragmentDev, boilerFragment);
                                         //   transaction.addToBackStack(null);
                                         transaction.commit();
                                     }
                                         if (type.contains(TYPE_LAZOSPARKING)) {
                                             Log.d("infoSensorLAZOS", "Split");
                                             FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                             LazosParkingFragment lazosParkingFragment = new LazosParkingFragment();
                                             FragmentTransaction transaction = fragmentManager.beginTransaction();
                                             transaction.replace(R.id.fragmentDev,lazosParkingFragment);
                                             //   transaction.addToBackStack(null);
                                             transaction.commit();
                                         }
                         }}

                  }


                public AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (!MainActivity.getInstance().mConnectionBT.bluetoothAdapter.isEnabled()) {
                            Toast.makeText(activity.getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //BluetoothStatus.setText(activity.getString(R.string.cConnet));
                        // Get the device MAC address, which is the last 17 chars in the View
                        String info = ((TextView) view).getText().toString();
                        final String address = info.substring(info.length() - 17);
                        final String name = info.substring(0, info.length() - 17);

                        // Spawn a new thread to avoid blocking the GUI one
                        new Thread() {
                            @Override
                            public void run() {
                                boolean fail = false;

                                BluetoothDevice device = MainActivity.getInstance().mConnectionBT.bluetoothAdapter.getRemoteDevice(address);

                                try {
                                    MainActivity.getInstance().mConnectionBT.bluetoothSocket = MainActivity.getInstance().mConnectionBT.createBluetoothSocket(device);
                                    dConnectedThread = new ConnectedThread(dConnectionBT.bluetoothSocket, dConnectionBT.mHandler);
                                } catch (IOException e) {
                                    fail = true;
                                    Toast.makeText(activity.getApplicationContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                                }
                                // Establish the Bluetooth socket connection.
                                try {
                                    dConnectionBT.bluetoothSocket.connect();
                                } catch (IOException e) {
                                    try {
                                        fail = true;
                                        dConnectionBT.bluetoothSocket.close();
                                        dConnectionBT.mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                                .sendToTarget();
                                    } catch (IOException e2) {
                                        //insert code to deal with this
                                        Toast.makeText(activity.getApplicationContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (!fail) {
                                    dConnectionBT.mConnectedThread = new ConnectedThread(dConnectionBT.bluetoothSocket, dConnectionBT.mHandler);
                                    dConnectionBT.mConnectedThread.start();

                                    dConnectionBT.mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                            .sendToTarget();
                                }
                            }
                        }.start();
                    }
                };


            }


*/