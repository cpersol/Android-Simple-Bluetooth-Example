package com.mcuhq.simplebluetooth;

import static android.app.PendingIntent.getActivity;
import static com.mcuhq.simplebluetooth.R.id.imageView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    public BluetoothConnection mConnectionBT;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TabActivity tabActivity;
    private ArrayAdapter<String> mBTArrayAdapter;
    private DeviceFragment mDeviceFragment;

    private IboxFragment iboxFragment;
    private GenericFragment genericFragment;
    private StaFragment staFragment;
    private DeviceFragment deviceFragment;
    private BluetoothFragment bluetoothFragment;
    private BoilerFragment boilerFragment;
    private static MainActivity instance;
    public String infoGeneric;
public Headers headers = new Headers();
    private final static String TYPE_IBOX = "IBOX";
    private final static String TYPE_STA = "STA";
    private final static String TYPE_BOILER = "BOILER";
    public String bluetoothMessage;
    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate (Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        setUpView();
        instance = this;
        this.mConnectionBT = new BluetoothConnection();
        setUpViewPagerAdapter();
    }

    private void setUpView(){
        tabLayout = findViewById(R.id.tabLayout);
        imageView =  (ImageView) ((AppCompatActivity) this).findViewById(R.id.imageView);
        appBarLayout = (AppBarLayout) ((AppCompatActivity) this).findViewById(R.id.appBarLayout);
        viewPager = (ViewPager) ((AppCompatActivity) this).findViewById(R.id.viewPager);
        tabActivity = new TabActivity(getSupportFragmentManager());
    }


    private void setUpViewPagerAdapter(){
        iboxFragment = new IboxFragment();
        boilerFragment = new BoilerFragment();
        staFragment = new StaFragment();
        bluetoothFragment = new BluetoothFragment();
        genericFragment = new GenericFragment();
        deviceFragment = new DeviceFragment();

        tabActivity.addFragment(bluetoothFragment, "BLUETOOTH");
        tabActivity.addFragment(genericFragment, "GENERIC");
        tabActivity.addFragment(deviceFragment, "DEVICE");

        viewPager.setAdapter(tabActivity);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:

                        imageView.setImageResource(R.drawable.ic_bt);
                        Log.d("TAG1", "Posicion: " + tabLayout.getSelectedTabPosition() + " Titulo: " + tabActivity.getPageTitle(tabLayout.getSelectedTabPosition()));

                        break;
                    case 1:
                        imageView.setImageResource(R.drawable.ic_generic);
                        Log.d("TAG1", "Posicion: " + tabLayout.getSelectedTabPosition() + " Titulo: " + tabActivity.getPageTitle(tabLayout.getSelectedTabPosition()));
                        if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null) //First check to make sure thread created
                         infoGeneric = headers.GET_GENERIC_INFO + headers.GET_GENERIC_DATA;
                        String infoGeneric_base64String=ProcessToSendMessage.hexToBase64(infoGeneric);
                        MainActivity.getInstance().mConnectionBT.mConnectedThread.write(infoGeneric_base64String);
                        break;
                    case 2:
                        imageView.setImageResource(R.drawable.ic_devices);
                        Log.d("TAG1", "Posicion: " + tabLayout.getSelectedTabPosition() + " Titulo: " + tabActivity.getPageTitle(tabLayout.getSelectedTabPosition()));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });
    }
    public String getBluetoothMessage() {
        return bluetoothMessage;
    }

    public void setBluetoothMessage(String message) {
        bluetoothMessage = message;
    }

}



