package com.mcuhq.simplebluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

public class BluetoothFragment extends Fragment  {

    private final String TAG = MainActivity.class.getSimpleName();

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    public static String readMessage = null;
    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private ListView mDevicesListView;
    private CheckBox mLED1;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Message msg;
    private Activity activity;
    private MainActivity Activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bluetooth, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(readMessage == null){
            Log.d("msg", "readMessage null");
        }
        MainActivity.getInstance().mConnectionBT.setActivityInMainActivity(getActivity());

        mBluetoothStatus = view.findViewById(R.id.bluetooth_status);
        MainActivity.getInstance().mConnectionBT.setBluetoothStatus(mBluetoothStatus);

        mReadBuffer = view.findViewById(R.id.read_buffer);
        MainActivity.getInstance().mConnectionBT.setBluetoothBufferTextView(mReadBuffer);

        mScanBtn = view.findViewById(R.id.scan);
        mOffBtn = view.findViewById(R.id.off);
        mDiscoverBtn = view.findViewById(R.id.discover);
        mListPairedDevicesBtn = view.findViewById(R.id.paired_btn);
        mLED1 = view.findViewById(R.id.checkbox_led_1);


        mBTArrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = view.findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);
        

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
           ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        MainActivity.getInstance().mConnectionBT.setupHandler();


        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText(getString(R.string.sBTstaNF));
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.sBTdevNF),Toast.LENGTH_SHORT).show();
        }
        else {

            mLED1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null) //First check to make sure thread created
                        MainActivity.getInstance().mConnectionBT.mConnectedThread.write("CARLA");
                }
            });

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  boolean responseOn =   MainActivity.getInstance().mConnectionBT.bluetoothOn();
                  if(responseOn){
                      mBluetoothStatus.setText(getString(R.string.BTEnable));
                  }
                  else{
                      mBluetoothStatus.setText("DISCONNECTED");
                      Toast.makeText(getActivity().getApplicationContext(),getString(R.string.BTisON),Toast.LENGTH_SHORT).show();
                  }
                }

            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    boolean responseOff= MainActivity.getInstance().mConnectionBT.bluetoothOff();
                    if(responseOff){
                        mBluetoothStatus.setText(getString(R.string.BTturOff));
                        Toast.makeText(getActivity().getApplicationContext(),getString(R.string.BTturOff),Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mBluetoothStatus.setText("DISENABLE");
                        Toast.makeText(getActivity().getApplicationContext(),getString(R.string.BTturOff),Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices();
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    MainActivity.getInstance().mConnectionBT.discover(mBTArrayAdapter, getActivity());
                    Toast.makeText(getActivity().getApplicationContext(),getString(R.string.Discover),Toast.LENGTH_SHORT).show();;
                }
            });
        }
    }


    public AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!MainActivity.getInstance().mConnectionBT.bluetoothAdapter.isEnabled()) {
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
                return;
            }

            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            new Thread()
            {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = MainActivity.getInstance().mConnectionBT.bluetoothAdapter.getRemoteDevice(address);

                    try {
                        MainActivity.getInstance().mConnectionBT.bluetoothSocket =  MainActivity.getInstance().mConnectionBT.createBluetoothSocket(device);
                        MainActivity.getInstance().mConnectionBT.mConnectedThread = new ConnectedThread( MainActivity.getInstance().mConnectionBT.bluetoothSocket,  MainActivity.getInstance().mConnectionBT.mHandler);

                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(activity.getApplicationContext() ,getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        MainActivity.getInstance().mConnectionBT.bluetoothSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            MainActivity.getInstance().mConnectionBT.bluetoothSocket.close();
                            MainActivity.getInstance().mConnectionBT.mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(activity.getApplicationContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        MainActivity.getInstance().mConnectionBT.mConnectedThread = new ConnectedThread(
                                MainActivity.getInstance().mConnectionBT.bluetoothSocket, MainActivity.getInstance().mConnectionBT.mHandler);
                        MainActivity.getInstance().mConnectionBT.mConnectedThread.start();

                        MainActivity.getInstance().mConnectionBT.mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };
    private void listPairedDevices(){
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.show_paired_devices), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity = (MainActivity) context;
    }

}



