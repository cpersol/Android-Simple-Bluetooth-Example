package com.mcuhq.simplebluetooth;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;


public class BluetoothConnection {
    private final static UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static int REQUEST_ENABLE_BT = 1;
    public final static int MESSAGE_READ = 2;
    private final static int CONNECTING_STATUS = 3;
    public static final int RESULT_OK = -1;
    private final String TAG = MainActivity.class.getSimpleName();
    private static TextView BluetoothStatus;
    private static TextView BluetoothBufferTextView;
    public static BluetoothAdapter bluetoothAdapter;
    private static Set<BluetoothDevice> pairedDevices;
    private static ArrayAdapter<String> arrayAdapter;
    public BluetoothSocket bluetoothSocket= null;
    private static Context context;
    public static String lastMessage;

    private final static String TYPE_IBOX = "IBOX";
    private final static String TYPE_STA = "STA";
    private final static String TYPE_BOILER = "BOILER";
    private Activity activity;
    protected Handler mHandler; // Our main handler that will receive callback notifications
    public ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    public String errorTextHexFormat = "Este texto no es válido, compruebe si es Hexadecimal";
    public BluetoothConnection () {
        this.context=context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lastMessage ="";
    }
    public void setBluetoothStatus(TextView t){
        BluetoothStatus = t;
    }
    public void setBluetoothBufferTextView(TextView t){
        BluetoothBufferTextView = t;
    }
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void enableBluetooth(Activity activity) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    public void setActivityInMainActivity(Activity act){
        this.activity = act;
    }
    public  boolean bluetoothOn(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.activity.startActivityForResult( enableBtIntent, REQUEST_ENABLE_BT);
            if (bluetoothAdapter.isEnabled()) {
            return  true;

            }
        }
        return true;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent Data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                BluetoothStatus.setText(context.getString(R.string.sEnabled));
            }
            else
                BluetoothStatus.setText(context.getString(R.string.sDisabled));
        }
    }

    public boolean bluetoothOff(){
        if (bluetoothAdapter.isEnabled()){
        bluetoothAdapter.disable();
        Toast.makeText(activity.getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
        return true;
        }
        return false;
    }


    public  BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };
    public void discover(ArrayAdapter _arrayAdapter,Activity act){
        arrayAdapter = _arrayAdapter;
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(context.getApplicationContext(),context.getString(R.string.DisStop),Toast.LENGTH_SHORT).show();
        }
        else{
            if(bluetoothAdapter.isEnabled()) {
                arrayAdapter.clear(); // clear items
                bluetoothAdapter.startDiscovery();
                Log.d("discover","buscancando devices");
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                this.activity.registerReceiver(blReceiver, filter);
            }
            else{
              Toast.makeText(activity.getApplicationContext(), context.getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean listPairedDevices(View view){

        arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if(bluetoothAdapter.isEnabled()) {
                return true;
         //Toast.makeText(context.getApplicationContext(), context.getString(R.string.show_paired_devices), Toast.LENGTH_SHORT).show();
        }
        else{
            return false;
        }
          //333Toast.makeText(context.getApplicationContext(), context.getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
    }


    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        bluetoothSocket = device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        return bluetoothSocket;
    }

    public void setupHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handleMessage(Message msg){

                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                    Log.d("READ_MESSAGE",readMessage);
                    BluetoothBufferTextView.setText(readMessage);
                    MainActivity.getInstance().setBluetoothMessage(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    char[] sConnected;
                   if(msg.arg1 == 1){
                       Log.e(TAG, "CONNECTED");
                    BluetoothStatus.setText(activity.getString(R.string.BTConnected) + msg.obj);
                   }
                    else{
                       Log.e(TAG, "DISCONNECTED");
                    BluetoothStatus.setText(activity.getString(R.string.BTconnFail));
                    }
                }
            }
        };
    }


}



