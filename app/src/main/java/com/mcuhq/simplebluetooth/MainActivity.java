package com.mcuhq.simplebluetooth;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    public BluetoothConnection mConnectionBT;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TabActivity tabActivity;
   // private DeviceFragment mDeviceFragment;
    public String infoGeneric;
    public Headers headers = new Headers();
    private GenericFragment genericFragment;
    private BluetoothFragment bluetoothFragment;

    private static MainActivity instance;

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
        bluetoothFragment = new BluetoothFragment();
        genericFragment = new GenericFragment();
      //  deviceFragment = new DeviceFragment();

        tabActivity.addFragment(bluetoothFragment, "BLUETOOTH");
        tabActivity.addFragment(genericFragment, "GENERIC");
      //  tabActivity.addFragment(deviceFragment, "DEVICE");

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
                       if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null){ //First check to make sure thread created
                            infoGeneric = headers.GET_GENERIC_INFO + headers.GET_GENERIC_DATA;
                        String infoGeneric_base64String=ProcessToSendMessage.hexToBase64(infoGeneric);
                        MainActivity.getInstance().mConnectionBT.mConnectedThread.write(infoGeneric_base64String);}
                        break;
                    /*case 2:
                        imageView.setImageResource(R.drawable.ic_devices);
                        Log.d("TAG1", "Posicion: " + tabLayout.getSelectedTabPosition() + " Titulo: " + tabActivity.getPageTitle(tabLayout.getSelectedTabPosition()));
                        if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null){ //First check to make sure thread created
                            infoDevice = headers.GET_DEVICE_INFO + headers.GET_GENERIC_DATA;
                            String infoDevice_base64String=ProcessToSendMessage.hexToBase64(infoDevice);
                            MainActivity.getInstance().mConnectionBT.mConnectedThread.write(infoDevice_base64String);}
                        break;*/
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



