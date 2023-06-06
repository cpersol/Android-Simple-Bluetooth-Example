# ***GUIÓN TÉCNICO GESINEN APP***
## **INTRODUCCIÓN**
Gesinen App ha sido diseñada y desarrollada para facilitar el control y la gestión de los dispisitivos y sensores de la empresa de manera eficiente y segura. Con esta herramienta, vamos a poder establecer conexiones seguras SSH y MQTT para acceder y controlar los dispositivos remotos de una manera cofiable. Asimismo, se podrá utilizar la conectividad Bluetooth para establecer una comuniación directa con los sensores y configurar genericamente acuerdo con las necesidades que queremos.

Además, esta aplicación te brinda la capacidad de acceder a la Plataforma de la empresa *Gesinen*.

## **INSTALACIÓN DE LA APP**
Esta aplicación está programada en Android Studio por lo que para poder instalar *Gesinen App* en tu dispositivo Android, solo hay que descargarsela y pedir los permisos necesarios. En este caso hay que dar perrmiso al debugger + permisos bluetooth desde los ajustes del teléfono.

## **ESTRUCTURA DEL CÓDIGO**
Esta aplicación nos ofrece 3 tipos de conexiones.Una sirve para conectarnos mediante Bluetooth a los sensores con los que trabja la empresa para poder configurar generícamente algunos de sus elementos. La segunda se trata de una conexión SSH que nos permite conectarnos con dicho protocolo con los Gateway. Y por último, tenemos la conexión MQTT para conectarnos al servidor privado de *Gesinen* o cualquier otro servidor privado.
### **ESTRUCTURA** 
#### *LOGIN*
* LoginActivity
Esta clase es la encargada de maejar la lógica de inició de sesión de una aplicación. Realiza una solicitud al servidor con los datos de inicio ed sesión ingresados por el usuario y maneja las respuestas del servidor.

#### *MENÚ*
* Menu

El siguiente código nos muestra la funcionalidad que tiene la clase menú. Esta formada por tres botones ( SENSORES (Bluetooth), GATEWAY (SSH y MQTT), y PLATAFORMA ) y una función llamada *irPlataforma*.
En el método onCreate, además de establecer la vista del layout "activity_menu" con *setContentView* y asignar los botones, se crea una instancia de la clase *LoginActivity* para obtener el *accesToken* necesario para poder reedirigir la plataforma directamente sin hacer otra vez un login. También se asignan los eventos clic a los botones:
* "Sensores": crea un intent para iniciar la actividad *MainActivity*
* "Gateway": crea un intent para iniciar la actividad *MenuGateway*
* "Plataforma": crea un intent para abrir la URL en un navegador externo. Para crear el intent utiliza la función *irPlataforma* pasando el *accesToken* como parámetro.
```java
public class Menu extends Activity {
    private Button Sensores;
    private Button Gateway;
    private Button Plataforma;
    private String AccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

         Sensores= (Button)findViewById(R.id.sensores);
         Gateway = (Button)findViewById(R.id.gateway);
         Plataforma = (Button)findViewById(R.id.plataforma);
         LoginActivity loginActivity= new LoginActivity();
         AccessToken=loginActivity.accessToken;

        Sensores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, MainActivity.class);
                startActivity(i);

            }
        });

        Gateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, MenuGateway.class);
                startActivity(i);

            }
        });

       Plataforma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            irAPlataforma(AccessToken);

            }
        });

    }

    private void irAPlataforma(String accessToken) {
        Uri uri = Uri.parse("https://swat-id.gesinen.com/#/sessions/signin");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Bundle extras = new Bundle();
        try {
            JSONObject json = new JSONObject();
            json.put("accessToken", accessToken);
            String headerValue =  json.toString();
            extras.putString("x-access-token", headerValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtras(extras);
        startActivity(intent);
    }

}

```
#### *SENSORES*
* **BLuetoothConnection**

Esta clase es la encargada de proporcionar a todas las clases que utilizan bluetooth, las funcionalidades necesarias para manejar la conexión Bluetooth. Una expliación breve de algunas partes clave del código son:

1. Variables estáticas y constantes: 
* 'BT_MODULE_UUID': Identificador UUID para el módulo Bluetooth utilizado en la conexión.
* 'REQUEST_ENABLE_BT': Código de solicitud utilizado para solicitar al usuario que habilite Bluetooth.
* 'MESSAGE_READ': Código de mensaje utilizado para indicar que se ha recibido un mensaje a través de Bluetooth.
* 'CONNECTING_STATUS': Es un código de estado utilizado para indicar el estado de la conexión Bluetooth.
* 'RESULT_OK': Código de resultado utilizado para indicar que una operación ha finalizado correctamente.
2. Variables y referencias de vistas:
* 'BluetoothStatus y BluetoothBufferTextView': Son referencias a vistas de texto utilizadas para mostrar el estado de Bluetooth y los datos recibidos.
* 'bluetoothAdapter': Es una instancia de BluetoothAdapter, que se utiliza para acceder a las funcionalidades de Bluetooth del dispositivo.
* 'pairedDevices': Es un conjunto de dispositivos Bluetooth emparejados.
* 'arrayAdapter': Es un adaptador utilizado para mostrar la lista de dispositivos Bluetooth emparejados.
* 'bluetoothSocket': Es un objeto BluetoothSocket utilizado para la conexión Bluetooth.
* 'mHandler': Es un controlador (Handler) utilizado para recibir notificaciones y mensajes de la conexión Bluetooth.
3. Métodos y funcionalidades:
* 'enableBluetooth()': Habilita Bluetooth si no está habilitado y solicita al usuario que lo habilite si es necesario.
* 'onActivityResult()': Se invoca cuando se obtiene una respuesta de una actividad, en este caso, la solicitud de habilitación de Bluetooth. Actualiza el estado de Bluetooth en función del resultado.
* 'blReceiver': Es un receptor de emisión (BroadcastReceiver) utilizado para recibir eventos de descubrimiento de dispositivos Bluetooth cercanos.
* 'discover()': Inicia el descubrimiento de dispositivos Bluetooth cercanos y actualiza la lista de dispositivos encontrados en el adaptador.
* 'listPairedDevices()': Obtiene y muestra los dispositivos Bluetooth emparejados en una lista.
* 'createBluetoothSocket()': Crea un socket Bluetooth para establecer la conexión con un dispositivo específico utilizando el identificador UUID.
* 'setupHandler()': Configura un controlador (Handler) para manejar los mensajes y notificaciones relacionados con la conexión Bluetooth.

```java
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
   // private Handler handler;

    public BluetoothSocket bluetoothSocket= null;
    private TextView ReadBuffer;
    public static String readMessage = null;
    private static Context context;
    private TextView textView;
    private TextView readBuffer;

    private TabLayout tabLayout;
    private View view;
    public static String lastMessage;

    private final static String TYPE_IBOX = "IBOX";
    private final static String TYPE_STA = "STA";
    private final static String TYPE_BOILER = "BOILER";
    private MainActivity mainActivity;
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
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                BluetoothStatus.setText(context.getString(R.string.sEnabled));
            }
            else
                BluetoothStatus.setText(context.getString(R.string.sDisabled));
        }
    }

    public boolean bluetoothOff(){
        if (bluetoothAdapter.isEnabled()){
        bluetoothAdapter.disable(); // turn off
         
        return true;
        }
        return false;
    }
    public  BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Se ha encontrado un dispositivo Bluetooth cercano
               
                // Hacer algo con el dispositivo encontrado
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };
    public void discover(ArrayAdapter _arrayAdapter,Activity act){
        // Check if the device is already discovering
        arrayAdapter = _arrayAdapter;
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
           
        }
        else{
            if(bluetoothAdapter.isEnabled()) {
                arrayAdapter.clear(); // clear items
                bluetoothAdapter.startDiscovery();
                Log.d("discover","buscancando devices");
              
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//               
                this.activity.registerReceiver(blReceiver, filter);
            }
            else{
       
            }
        }
    }

    public boolean listPairedDevices(View view){

        arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1);
        arrayAdapter.clear();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if(bluetoothAdapter.isEnabled()) {
       
                return true;

        }
        else{
            return false;
        }
    
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
```


* **ConnectedThread**

Esta clase se utiliza para manejar una conexión Bluetooth establecida con un dispositivo remoto.
Tiene los siguientes atributos:
* 'mmSocket': una instancia de BluetoothSocket, que representa el socket Bluetooth utilizado para la conexión.
* 'mmInStream': una instancia de InputStream, que se utiliza para leer datos de entrada desde el socket Bluetooth.
* 'mmOutStream': una instancia de OutputStream, que se utiliza para escribir datos de salida en el socket Bluetooth.
* 'mHandler': una instancia de Handler, que se utiliza para enviar mensajes a la actividad de interfaz de usuario (UI activity).

El constructor de ConnectedThread toma un objeto BluetoothSocket y un objeto Handler como argumentos. Dentro del constructor, se obtienen las instancias de InputStream y OutputStream a partir del socket Bluetooth proporcionado.

* Métodos y funcionalidades:

1. 'run()': se sobrescribe para ejecutar la lógica principal del subproceso. En este caso, el subproceso se mantiene en un bucle infinito que escucha el InputStream en busca de datos de entrada. Cuando se detectan datos disponibles, se leen del InputStream, se empaquetan en un mensaje y se envían al Handler para su procesamiento en la interfaz de usuario.

2. 'write(String input)': se utiliza para enviar datos al dispositivo remoto. Los datos se convierten en bytes y se escriben en el OutputStream asociado al socket Bluetooth.

3. 'cancel()': se utiliza para cerrar la conexión Bluetooth al llamar al método close() en el BluetoothSocket.

```java
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.available();
                if(bytes != 0) {
                    buffer = new byte[1024];
                    SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                    bytes = mmInStream.available(); // how many bytes are ready to be read?
                    bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                    mHandler.obtainMessage(BluetoothFragment.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget(); // Send the obtained bytes to the UI activity
                }
            } catch (IOException e) {
                e.printStackTrace();

                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) {
        Log.d("WRITE",input);
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

```


* **Headers**

En esta clase se han definido distintas cabeceras para los mensajes.
```java
public class Headers {
    public static  String GET_GENERIC_DATA = "";
    public static String SET_APPKEY = "";
    public static String GET_APPKEY = "";
    public static String SET_DEVEUI = "";
    public static String GET_DEVEUI = "";
    public static String SET_TIME = "";
    public static String GET_TIME = "";
    public static String GET_GENERIC_INFO="";
    public static String GET_DEVICE_INFO="";

    public Headers() {
        this.GET_GENERIC_DATA = "00";
        this.GET_GENERIC_INFO = "A0";
        this.SET_APPKEY = "A1";
        this.SET_DEVEUI = "A2";
        this.SET_TIME = "A3";
        this.GET_DEVICE_INFO = "B0";

    }
}
```
* **MainActivity**

Esta clase contiene dos fragmentos llamados *bluetoothFragment* y *genericFragment* y es la encargada de cofigurar los fragmentos, establecer los adaptadores de vista de las páginas y  se configuran los eventos de selección de pestaña. También tiene una instacia llamada *mConnectionBT* de la clase *BluetoothConnection* para administrar la conexión Bluetooth y dos métodos para obtener y establecer mensajes Bluetooth (*getBluetoothMessage()* y *setBluetoothMessage()*)

En el evento *onTabSelected()* se definen las acciones a realizar según la posición de la pestaña seleccionada:

1. Si se encuentra en el tab[0], nos muestra el tab referido a 'BLUETOOTH' donde se van a realizar las conexiones Bluetooth.
2. Si está en la posición tab[1] nos mostrará el tab de 'GENERIC' donde se van poder configurar genéricamente los sensores.


Además, como podemos observar en el código, cuando pasamos al tab[1] (GENERIC), si se ha realizado correctamente la conexión, se envía a través de Bluetooth un mensaje formado por una cabecera de indentificación "A0" (GET_GENERIC_INFO) y otra de datos "00" (GET_GENERIC_DATA), para que el sensor nos envie la información que queremos. 

```java
public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    public BluetoothConnection mConnectionBT;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TabActivity tabActivity;
    private ArrayAdapter<String> mBTArrayAdapter;
   // private DeviceFragment mDeviceFragment;
    public String infoGeneric;
    public String infoDevice;
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
```


* **ProcessToSendMessage**

Esta clase contiene dos métodos relacionados con la conversión de cadenas hexadecimal a base64 y la verificación de si una cadena es hecadecimal. Sus funcionalidades son :
* 'hexToBase64': tranforma una cadena hexadecimal en una cadena de texto en formato base64. Para ello, se divide la cadena hexadecimal en bytes, se convierten los bytes a enteros y se almacenan en un arreglo de bytes. Luego,con la clase 'android.util.Base64', se codifican los bytes en una cadena base64.
* 'isHexadecimal': método que verifica si la cadena dada es hexadecimal.En este caso, se utiliza una expresión regular (0-9,A-F o a-f) para verificar si la cadena contiene solo carácteres hexadecimales.

```java
public class ProcessToSendMessage {

 public static String hexToBase64(String hexString) {
  if (hexString != null && !hexString.isEmpty()) {
   byte[] hexBytes = new byte[hexString.length() / 2];
   for (int i = 0; i < hexBytes.length; i++) {
    int index = i * 2;
    int intValue = Integer.parseInt(hexString.substring(index, index + 2), 16);
    hexBytes[i] = (byte) intValue;
   }
   String base64String = android.util.Base64.encodeToString(hexBytes, android.util.Base64.DEFAULT);
   return base64String;
  }
  return null;
 }

 public static boolean isHexadecimal(String str) {
  Pattern pattern = Pattern.compile("^[0-9A-Fa-f]+$");
  return pattern.matcher(str).matches();
 }
}
```
##### TAB
* TabActivity

Esta clase se utiliza para administrar los fragmentos de un diseño de pestañas (MainActivity) y esta formanda por:
* Dos listas ('fragments' y 'fragmentsTitles') que se utilizan para almacenar los fragmentos y los t´tulos respectivos de las pestañas.
* Y por 5 métodos:
1. 'getItem()': se sobrescribe para devolver el fragmento correspondiente a la posición específica. Para ello utiliza 'fragments' para obtener el fragmento basado en la posición y lo devuelve.
2. 'getCount': se sobrescribe para devolver el número de fragmentos total para determinar la cantidad de pestañas que se mostrarán.
3. 'getPageTitle()': devuelve el título de la pestaña correspondiente.
4. 'addFragment()': permite agregar dinámicamente un fragmento con su tútlo correspondiente.
```java
public class TabActivity extends FragmentPagerAdapter {

    private List <Fragment> fragments=new ArrayList<>();
    private List <String> fragmentsTitles=new ArrayList<>();

    public TabActivity(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
       return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragmentsTitles.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        fragments.add(fragment);
        fragmentsTitles.add(title);
    }
}

```

###### TAB BLUETOOTH
* BluetoothFragment

Esta clase se trata de uno de los fragmentos que contiene *MainActivity*, en concreto el fragmento con la funcionalidad Bluetooth para poder crear la conexión con el sensor al que vayamos a conectarnos.Sus características son:

* Contiene constantes para identificar diferentes estas y mensajes relacionados con la comunicación Bluetooth. 
* En el método *onCreateView* se infla el diseño del fragmento y se configuran los componentes de la interfaz de usuario. También se solicita el permiso de ubicación si aún no se ha otorgado.
* En el método *onViewCreated()*, se configuran los controladores de eventos para los botones y se inicializan los adaptadores y objetos relacionados con Bluetooth. También se realiza la detección de dispositivos y se manejan las interacciones con los dispositivos Bluetooth emparejados. Se definen 4 botones y una Checkbox:
1. 'mLED1': Checkbox que sirve para enviar un mensaje por Bluetooth al terminal del sensor. Si se ha realizado bien la conexión, podremos comprobar que se ha recibido en el terminal del sensor: "CARLA".
2. 'mScanBtn': Botón para activar el Bluetooth.
3. 'mOffBtn': Botón para desacivar el Bluetooth.
4. 'mListPairedDevices': Botón que muestra lista de dispositivos emparejados.
5. 'mDiscoverBtn': Botón para buscar nuevos dispositivos cercanos a través de Bluetooth.
* Se define el método 'lisPairedDevices()' para mostrar los dispositivos Bluetooth emparejados.

```java
public class BluetoothFragment extends Fragment  {
    
    private final String TAG = MainActivity.class.getSimpleName();

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
   
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    public static final int RESULT_OK = -1;
    private final static String TYPE_IBOX = "IBOX";
    private final static String TYPE_STA = "STA";
    private final static String TYPE_BOILER = "BOILER";
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
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bluetooth, container, false);
       // MainActivity.getInstance().mConnectionBT.setActivityInMainActivity(getActivity());

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
                   /* if(MainActivity.getInstance().mConnectionBT.mConnectedThread != null){//First check to make sure thread created
                        infoGeneric = headers.GET_GENERIC_INFO + headers.GET_GENERIC_DATA;
                        String infoGeneric_base64String=ProcessToSendMessage.hexToBase64(infoGeneric);
                        MainActivity.getInstance().mConnectionBT.mConnectedThread.write(infoGeneric_base64String);
                    }*/
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

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
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
```


###### TAB GENERIC
* GenericFragment
Esta clase se trata del segungo fragmento que contiene *MainActivity*. Aquí se pueden algunos de los valores genéricos en los sensores.

Las funcionalidades que tienes esta clase a travñes de los controladores de eventos osn los siguientes:
* 'buttonTerminal':Obtiene un mensaje de Bluetooth desde la clase MainActivity y lo muestra en la vista de texto mTerminal.
* 'buttonAppKey':Obtiene el texto del campo de texto appKeyText, comprueba está en formato hexadecimal,lo convierte a una cadena hexadecimal y luego a una cadena en base64.Una vez en base64 envía la cadena a través de Bluetooth para actualizar su valor en memoria.
* 'buttonDeveui': realiza a misma función que 'buttonAppKey' pero con Deveui.
* 'buttonTimeTx':Obtiene el texto del campo de texto timeTxText y lo envía a la clase MainActivity utilizando la conexión Bluetooth.
* 'buttonInput': envia el comando recogido en 'inputText' al sensor al través de Bluetooth.
* 'buttonGetConfigSensor': Obtiene un mensaje de Bluetooth desde la clase *MainActivity* y lo procesa para extraer información específica. Una vez procesada la información, nos mostrara en los campos de texto 'appketText','deveuiText' y 'timeTx' la información extraída del mensaje recibido.

Como se ha explicado en el apartado de *MainActivity* cuando cambiamos de tab, el dispositivo envía un mensaje a través de Bluetooth al sensor. Cuando este recibe el mensaje con dichas cabeceras, devuelve a través de la conexión Bluetooth un mensaje compuesto por: A0_APPKEY_DEVEUI_TIMETX.  Por lo que el botón 'buttonGetConfigSensor', recoge este mensaje si se ha realizado correctamente la conexión entre el dispositivo y el sensor, lo procesa y así es como nos muestra por los campos de texto sus respectivos valores.

```java
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
    private String interval_intString;
    private String interval_base64String;
    public Headers headers = new Headers();
    private String input_hexString;
    private TextView textViewAPPKEY;
    private TextView textViewDEVEUI;
    private TextView textViewINTERVAL;
    private TextView textViewINPUT;


    private TextView mTerminal;
    private String informacionRecibida;
    private MainActivity mainActivity;
    public String readMessage;
    private TextView gBluetoothStatus;
    private TextView gReadBuffer;
    public String infoGeneric;

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
                //textViewINTERVAL.setText(MainActivity.getInstance().mConnectionBT.errorTextHexFormat);
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
```


#### *GATEWAY*
##### MENÚ GATEWAY
* MenuGateway

MenuGateway es una clase formada por únicamente dos botones.En el método *onCreate* se establece la vista del layout ("activity_menu_gateway"), se asignan los botones con sus correspondientes ID y se asignan los eventos clic a los botones:
* "SSH": crea un intent para iniciar la actividad *SSHCONNECTION*.
* "MQTT": crea un intent para iniciar la actividad *MQTTConnection*.

```java
public class MenuGateway extends Activity {
    private Button Ssh;
    private Button Mqtt;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_menu_gateway);

        Ssh=(Button)findViewById(R.id.SshButton);
        Mqtt=(Button)findViewById(R.id.MqttButton);


        Ssh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuGateway.this, SSHCONECT.class);
                startActivity(i);

            }
        });


        Mqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuGateway.this, MQTTConnection.class);
                startActivity(i);

            }
        });
    }
}
```

###### SSH
* SSHCONNECTION

Esta clase se trata de una actividad de Android que permite establecer una conexión SSH a un servidor remoto, ejecutar comandos SSH y ver el resultado de los comandos en una vista de texto.
* En el método *onCreate* se infla el diseño y se configuran sus elementos, se obtienen los valores de los campos de texto 'usuario','pass' y 'hostname' y se asignan a las variables 'username', 'password' e 'ip' respectivamente y también se configuran varios botones:
1. 'crearConexion':ejecuta un nuevo hilo en segundo plano mediante la clase AsyncTask. Dentro de este hilo, se llama al método executeRemoteCommand para ejecutar un comando remoto SSH utilizando los valores de 'username', 'password' e 'ip'. El resultado del comando se muestra en la vista de texto terminal.
2. 'enviarComando': realiza la misma función que 'crearConexión' con la diferencia de que este recoge el comando que hay en el campo de texto 'CommandField' y lo envia a través de la conexión que se crea.
3. 'r500': este controlador llama al método 'redicreccionar5000' que obtiene el valor del campo de texto 'hostname' y abre la URL correspondiente en un navegador externo a través de la clase 'Intent' con el puerto 5000.
4. 'r8080':este botón llama al método 'redireccionar8080'. Este realiza el mismo procedimiento que 'redireccionar5000' pero con el puerto 8080.

El método 'executeCommand' se encarga de establecer una conexión SSH utilizando la biblioteca JSch y ejecutar un comando remoto en el servidor SSH especificado. El resultado del comando se captura y se devuelve como una cadena. Además, se actualiza la vista de texto terminal con el resultado del comando.

```java
public class SSHCONECT extends AppCompatActivity {

    private static JSch jsch;

    static EditText usuario;
    static EditText hostname;
    static EditText pass;
    public Toast t;
    static EditText commandText;
    static String command;
    static TextView terminal;
    static String outputSSH="";
    static Button r5000;
    static Button r8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ssh);

        usuario = (EditText) findViewById(R.id.UsernameField);
        pass = (EditText) findViewById(R.id.PasswordField);
        hostname = (EditText) findViewById(R.id.IpField);
        commandText= (EditText)findViewById(R.id.CommandField);
        terminal= (TextView) findViewById(R.id.sshTextView);
        r5000= (Button) findViewById(R.id.decoder5000Button);
        r8080= (Button) findViewById(R.id.chirpstack8080Button);




        final String username = usuario.getText().toString();
        final String password = pass.getText().toString();
        final String ip = hostname.getText().toString();


        Button crearConexion= (Button) findViewById(R.id.ButtonCrearConexion);
        Button enviarComando= (Button) findViewById(R.id.ButtonComando);





        crearConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*EJECUTAR EN SEGUNDO PLANO*/
                new AsyncTask<String, Void, Void>() {

                    @Override
                    protected Void doInBackground(String... params) {
                        try {

                            String username = params[0];
                            String password = params[1];
                            String ip = params[2];

                            outputSSH = executeRemoteCommand(username, password, ip, 22);
                            Log.d("return",outputSSH);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    terminal.setText(outputSSH);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                        return null;
                    }
                }.execute(username, password, ip);

            }
        });


        enviarComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, Void>() {

                    @Override
                    protected Void doInBackground(String... params) {
                        try {

                            String username = params[0];
                            String password = params[1];
                            String ip = params[2];

                            String outputSSH = executeRemoteCommand(username, password, ip, 22);
                            Log.d("return",outputSSH);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    terminal.setText(outputSSH);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                        return null;
                    }
                }.execute(username, password, ip);

            }
        });
        r5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redireccionar5000(v);
            }
        });
        r8080.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redireccionar8080(v);
            }
        });


    }

    /*METODO EJECUTAR COMANDO*/
    public static String executeRemoteCommand(String username, String password, String ip,
                                              int port)
            throws Exception {

            command=commandText.getText().toString();

            username = usuario.getText().toString();

            password = pass.getText().toString();

            ip = hostname.getText().toString();

            port = 22;

            jsch = new JSch();

            Session sesion = jsch.getSession(username, ip, port);
            Log.d("SESION", "Session Get");
            sesion.setPassword(password);
            Log.d("SESION", "Session pass");

            /*PREGUNTAR POR INTERCAMBIO ESTRICTO DE LLAVES*/

            Properties prop = new Properties();

            prop.put("StrictHostKeyChecking", "no");

            sesion.setConfig(prop);
            Log.d("SESION", "Session setConfig");

            /*CONECTAMOS LA SESION*/

            sesion.connect();

            /*CONFIGURACION DEL CANAL SSH*/
            Channel canalssh = sesion.openChannel("exec");

            // ChannelExec canalssh = new ChannelExec();

            // sesion.openChannel("exec");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            /*EJECUTAMOS EL COMANDO*/
            command=commandText.getText().toString();

            ((ChannelExec) canalssh).setCommand((command != "")?command:"pwd");

            Log.d("COMMAND", "comando");

            canalssh.setOutputStream(baos);

            canalssh.connect(5000);

            // canalssh.disconnect();
            // canalssh.getOutputStream();
            while (!canalssh.isClosed()) {
                Thread.sleep(1000);
            }

            canalssh.disconnect();


         outputSSH = baos.toString();
        terminal.post(new Runnable() {
            public void run() {
                terminal.setText(outputSSH);
            }
        });
        return baos.toString();

    }


    public void redireccionar5000(View view) {
        String ip=hostname.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://" + ip + ":5000"));
        startActivity(intent);
    }

    public void redireccionar8080(View view) {
        String ip=hostname.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://" + ip + ":8080"));
        startActivity(intent);
    }

    }
```

###### MQTT
* MQTTConnection

Esta clase representa una actividad de Android que implementa la funcionalidad de conexión y comunicación con el servidor MQTT privado de Gesinen o cualquier otro . Proporciona métodos para establecer una conexión MQTT, publicar mensajes, suscribirse a temas y desconectar la conexión.

La variable más destaca es *client* que es una instancia de la clase MqttAndroidClient, que proporciona una API para interactuar con un servidor MQTT (Message Queuing Telemetry Transport).

Y los métodos más relevantes:
1. 'conn': Método utilizado para establecer una conexión MQTT con las opciones de conexión especificadas. Se llama al método connect de la instancia client y se manejan los callbacks de éxito o fallo de la conexión.
2. 'published':Método llamado cuando se hace clic en el botón de publicación. Obtiene los valores de los campos de texto de la interfaz de usuario y publica un mensaje en el tema MQTT especificado.
3. 'setSuscrption':Método llamado cuando se hace clic en el botón de suscripción. Obtiene el tema de suscripción de la interfaz de usuario y se suscribe a ese tema.
4. 'disconn':Método utilizado para desconectar la conexión MQTT. Se llama al método disconnect de la instancia client y se manejan los callbacks de éxito o fallo de la desconexión.
5. 'getTrustManagers':Método utilizado para obtener una matriz de administradores de confianza personalizados. En este caso, se crea un administrador de confianza que no realiza ninguna verificación adicional en el lado del cliente y no acepta ningún certificado del servidor.

```java
public class MQTTConnection extends AppCompatActivity {

    MqttAndroidClient client;
    TextView subText;
    EditText topicS;
    EditText topicP;
    EditText msg;
    String topicSuscribe;
    String topicPublish;
    String mensaje;
    String brokerUrl ;
    String username ;
    String password ;
    String clientId = MqttClient.generateClientId();
    Button anotherServer;
    LinearLayout llContenido;
    EditText newBrokerPort;
    EditText newUsername;
    EditText newPassword;
    Button anotherConnection;
    Button connect;
    String NewBrokerPort;
    String NewUsername;
    String NewPassword;
    Button suscribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt1);

        subText = findViewById(R.id.subText);
        anotherServer=findViewById(R.id.btnAnotherServer);
        llContenido = findViewById(R.id.llContenido);
        newBrokerPort = findViewById(R.id.newBroker);
        newUsername = findViewById(R.id.newUsername);
        newPassword = findViewById(R.id.newPassword);
        anotherConnection = findViewById(R.id.connectAnotherServer);
        connect = findViewById(R.id.connBtn);
        topicS=findViewById(R.id.topicSuscribe);
        suscribe=findViewById(R.id.btnSuscribe);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username="gesinen";
                password="gesinen2110";
                brokerUrl="ssl://gesinen.es:8882";
                client = new MqttAndroidClient(MQTTConnection.this, brokerUrl, clientId);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(username);
                options.setPassword(password.toCharArray());
                // Configuración de SSL/TLS
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, getTrustManagers(), null);
                    SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                    options.setSocketFactory(socketFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                    }
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        subText.setText(new String(message.getPayload()));
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
                conn(v,options);
            }
        });
        anotherConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 NewBrokerPort=newBrokerPort.getText().toString();
                 NewUsername=newUsername.getText().toString();
                 NewPassword=newPassword.getText().toString();
                client = new MqttAndroidClient(MQTTConnection.this, NewBrokerPort, clientId);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(NewUsername);
                options.setPassword(NewPassword.toCharArray());
                conn(v,options); // Llama al método conn(View v) cuando se presione el botón
                // Configuración de SSL/TLS
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, getTrustManagers(), null);
                    SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                    options.setSocketFactory(socketFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                    }
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        subText.setText(new String(message.getPayload()));
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
                conn(v,options);
            }
        });

        anotherServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llContenido.getVisibility() == View.VISIBLE) {
                    llContenido.setVisibility(View.GONE);
                } else {
                    llContenido.setVisibility(View.VISIBLE);
                }
            }
        });
        suscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicSuscribe= topicS.getText().toString();
                setSubscription(topicSuscribe);
                Log.d("suscribe",topicSuscribe);
            }
        });

    }

    public void published(View v){
        //String topic = "topictesting";
        //String message = "HOLA SOY CARLOTA";
        topicP=findViewById(R.id.topicPublish);
        topicPublish=topicP.getText().toString();
        msg= findViewById(R.id.msg);
        mensaje=msg.getText().toString();

        try {
            client.publish(topicPublish, mensaje.getBytes(),0,false);


            Toast.makeText(this,"Published Message",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }
 //TODO: topin pra suscribirse debería ser fijo (borrar textview+tostring)PREG A BUCHU
    private void setSubscription(String topicSubscribe){
        //topicS=findViewById(R.id.topicSuscribe);
        //topicSuscribe= topicS.getText().toString();

        try{

            client.subscribe(topicSubscribe,0);


        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v,MqttConnectOptions options){

        try {
            IMqttToken token = client.connect(options);
            Log.d("connection",clientId);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MQTTConnection.this,"connected!!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MQTTConnection.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    public void disconn(View v){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MQTTConnection.this,"Disconnected!!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MQTTConnection.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

   private TrustManager[] getTrustManagers() {
       try {
           // Crear un administrador de confianza personalizado
           X509TrustManager trustManager = new X509TrustManager() {
               @Override
               public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                   // No es necesario implementar esta verificación en el lado del cliente
               }

               @Override
               public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                   // Aquí puedes realizar la validación personalizada del certificado del servidor si es necesario
                   // Por ejemplo, puedes verificar la cadena de certificados o realizar otras comprobaciones

                   // Si confías en el certificado del servidor sin ninguna validación adicional, puedes omitir esta implementación
               }

               @Override
               public X509Certificate[] getAcceptedIssuers() {
                   return new X509Certificate[0]; // No es necesario para este caso
               }
           };

           return new TrustManager[] { trustManager };
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }
}
```