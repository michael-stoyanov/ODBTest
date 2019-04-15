package com.example.obdtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obdtest.commands.ObdCommand;
import com.example.obdtest.commands.control.VinCommand;
import com.example.obdtest.commands.engine.RPMCommand;
import com.example.obdtest.commands.engine.ThrottlePositionCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    WifiManager wifiManager;
    WifiConfiguration wifiConfig;

    ListView networks;
    TextView result_text;

    //Button wifiManager_enable;
    Button wifiManager_scan;
    Button sendVinCommand;
    Button hardcodedVin;
    Button getHotspotIpAddress;
    Button getMyIpAddress;
    Button vLinkConnect;

    List<ScanResult> scanResultList;

    String IPaddress;

    public String result;
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            scanResultList = wifiManager.getScanResults();
            String deviceArray[] = new String[scanResultList.size()];

            for (int i = 0; i < scanResultList.size(); i++) {
                deviceArray[i] = scanResultList.get(i).SSID;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceArray);

            networks.setAdapter(adapter);
            networks.setVisibility(View.VISIBLE);

            unregisterReceiver(this);
        }
    };
    private ClientClass client;
    private Socket clientSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        networks = (ListView) findViewById(R.id.wifi_networks_listView);
        networks.setVisibility(View.INVISIBLE);

        result_text = (TextView) findViewById(R.id.result_text);

        wifiManager_scan = (Button) findViewById(R.id.wifi_search);
        //wifiManager_enable = (Button) findViewById(R.id.wifi_enable);

        sendVinCommand = (Button) findViewById(R.id.sendVinNoCommand);
        hardcodedVin = (Button) findViewById(R.id.hardcodedVin);

        getHotspotIpAddress = (Button) findViewById(R.id.getHotspotIpAddress);
        getMyIpAddress = (Button) findViewById(R.id.getMyIpAddress);

        vLinkConnect = (Button) findViewById(R.id.VLinkConnect);
        setOnClickListeners();
    }

    public void setOnClickListeners() {
        //wifiManager_enable.setOnClickListener(this);

        wifiManager_scan.setOnClickListener(this);

        sendVinCommand.setOnClickListener(this);
        hardcodedVin.setOnClickListener(this);

        getHotspotIpAddress.setOnClickListener(this);
        getMyIpAddress.setOnClickListener(this);

        vLinkConnect.setOnClickListener(this);

        networks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult device = scanResultList.get(position);

                wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", device.SSID);

                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                int inetId = wifiManager.addNetwork(wifiConfig);

                wifiManager.disconnect();
                wifiManager.enableNetwork(inetId, true);
                wifiManager.reconnect();

                networks.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void VLinkConnect() {
        wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = "V-Link";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        int inetId = wifiManager.addNetwork(wifiConfig);

        wifiManager.disconnect();
        wifiManager.enableNetwork(inetId, true);
        wifiManager.reconnect();

    }

    @SuppressWarnings("deprecation")
    public void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
        Toast.makeText(this, "Scanning .....", Toast.LENGTH_LONG).show();
    }

//    StringBuilder res = new StringBuilder();
//
//    private static Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
//    private static Pattern BUSINIT_PATTERN = Pattern.compile("(BUS INIT)|(BUSINIT)|(\\.)");
//    private static Pattern SEARCHING_PATTERN = Pattern.compile("SEARCHING");
//    private static Pattern DIGITS_LETTERS_PATTERN = Pattern.compile("([0-9A-F])+");
//
//    protected String removeAll(Pattern pattern, String input) {
//        return pattern.matcher(input).replaceAll("");
//    }
//
//    protected ArrayList<Integer> buffer = null;
//    protected String cmd = "010C";
//    protected boolean useImperialUnits = false;
//    protected String rawData = null;
//    protected Long responseDelayInMs = null;
//    private long start;
//    private long end;

    int Port;

    @Override
    public void onClick(View btn) {
        try {
            switch (btn.getId()) {
//                case R.id.wifi_enable:
//                    if (!wifiManager.isWifiEnabled()) {
//                        Toast.makeText(getApplicationContext(), "Wifi    is disabled..making it enabled", Toast.LENGTH_LONG).show();
//                        wifiManager.setWifiEnabled(true);
//                    }
//                    break;
//                case R.id.wifi_search:
//                    networks = (ListView) findViewById(R.id.wifi_networks_listView);
//                    scanWifi();
//                    break;
//                case R.id.VLinkConnect:
//                    VLinkConnect();
//                    break;
                case R.id.getMyIpAddress:
//                    IPaddress = GetDeviceIpWiFiData();
//                    result_text.setText(IPaddress);

                    break;
                case R.id.getHotspotIpAddress:
                    IPaddress = intToInetAddress(wifiManager.getDhcpInfo().serverAddress).getHostAddress();
                    result_text.setText(IPaddress);
                    break;
                case R.id.sendVinNoCommand:
                    break;
                case R.id.hardcodedVin:
                    try {

                        final AsyncTask task = new ClientClass(new ThrottlePositionCommand(), new ClientClass.ObdCommandResponse() {
                            @Override
                            public void getObdFormattedResponse(String response) {
                                result_text.setText(response);

                            }
                        }).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                                if (clientSocket == null || !clientSocket.isConnected())
//                                    clientSocket = new Socket("192.168.0.10", 35000);
//
//                                OutputStream out = clientSocket.getOutputStream();
//                                InputStream in = clientSocket.getInputStream();
//
//                                cmd = "01 11";
//                                sendCommand(out);
//                                buffer = new ArrayList<>();
//                                result = readRawData(in);
//                                fillBuffer();
//                                //buffer = result.substring(result.indexOf("\r"));
//                                //int km = buffer.get(2) * 256 + buffer.get(3);
//                                //int rpm = (buffer.get(2) * 256 + buffer.get(3)) / 4;
//
//                                cmd = "AT DPN";
//                                cmd = "AT DPN";
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
//            result_text.setTexext(e.getMessage());
            Toast.makeText(this, "Better luck next time!", Toast.LENGTH_SHORT).show();
        }
    }

    //    protected void readResult(InputStream in) throws IOException {
//        readRawData(in);
//        checkForErrors();
//        fillBuffer();
//        performCalculations();
//    }
//
//    private final Class[] ERROR_CLASSES = {
//            UnableToConnectException.class,
//            BusInitException.class,
//            MisunderstoodCommandException.class,
//            NoDataException.class,
//            StoppedException.class,
//            UnknownErrorException.class,
//            UnsupportedCommandException.class
//    };
//
//    void checkForErrors() {
//        for (Class<? extends ResponseException> errorClass : ERROR_CLASSES) {
//            ResponseException messageError;
//
//            try {
//                messageError = errorClass.newInstance();
//                messageError.setCommand(this.cmd);
//            } catch (InstantiationException e) {
//                throw new RuntimeException(e);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//
//            if (messageError.isError(rawData)) {
//                throw messageError;
//            }
//        }
//    }
//
//    protected void sendCommand(OutputStream out) throws IOException,
//            InterruptedException {
//        // write to OutputStream (i.e.: a BluetoothSocket) with an added
//        // Carriage return
//        out.write((cmd + "\r").getBytes());
//        out.flush();
//        if (responseDelayInMs != null && responseDelayInMs > 0) {
//            Thread.sleep(responseDelayInMs);
//        }
//    }
//
//    protected void fillBuffer() {
//        rawData = removeAll(WHITESPACE_PATTERN, rawData); //removes all [ \t\n\x0B\f\r]
//        rawData = removeAll(BUSINIT_PATTERN, rawData);
//
//        if (!DIGITS_LETTERS_PATTERN.matcher(rawData).matches()) {
//            throw new NonNumericResponseException(rawData);
//        }
//
//        // read string each two chars
//        //buffer.clear();
//        int begin = 0;
//        int end = 2;
//        while (end <= rawData.length()) {
//            buffer.add(Integer.decode("0x" + rawData.substring(begin, end)));
//            begin = end;
//            end += 2;
//        }
//    }
//
//    protected void performCalculations() {
//        result = rawData;
//        String workingData;
//        if (result.contains(":")) {//CAN(ISO-15765) protocol.
//            workingData = result.replaceAll(".:", "").substring(9);//9 is xxx490201, xxx is bytes of information to follow.
//            Matcher m = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE).matcher(convertHexToString(workingData));
//            if (m.find()) workingData = result.replaceAll("0:49", "").replaceAll(".:", "");
//        } else {//ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
//            workingData = result.replaceAll("49020.", "");
//        }
//        result = convertHexToString(workingData).replaceAll("[\u0000-\u001f]", "");
//    }
//
//    protected String readRawData(InputStream in) throws IOException {
//        byte b;
//        StringBuilder res = new StringBuilder();
//
//        // read until '>' arrives OR end of stream reached (and skip ' ')
//        char c;
//        while (true) {
//            b = (byte) in.read();
//            if (b == -1) // -1 if the end of the stream is reached
//            {
//                break;
//            }
//            c = (char) b;
//            if (c == '>') // read until '>' arrives
//            {
//                break;
//            }
//            if (c != ' ') // skip ' '
//            {
//                res.append(c);
//            }
//        }
//
//        rawData = res.toString().trim();
//        return rawData;
//    }
//
//    //    protected void readRawData(InputStream in) throws IOException {
//        byte b = 0;
//
//        // read until '>' arrives OR end of stream reached
//        char c;
//        // -1 if the end of the stream is reached
//        while (((b = (byte) in.read()) > -1)) {
//            c = (char) b;
//            if (c == '>') // read until '>' arrives
//            {
//                break;
//            }
//            res.append(c);
//        }
//
//        rawData = removeAll(SEARCHING_PATTERN, res.toString());
//
//    }
//
//    public String convertHexToString(String hex) {
//        StringBuilder sb = new StringBuilder();
//        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
//        for (int i = 0; i < hex.length() - 1; i += 2) {
//
//            //grab the hex in pairs
//            String output = hex.substring(i, (i + 2));
//            //convert hex to decimal
//            int decimal = Integer.parseInt(output, 16);
//            //convert the decimal to character
//            sb.append((char) decimal);
//        }
//        return sb.toString();
//    }
//

    public InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //
//    public String GetDeviceIpWiFiData() {
//        @SuppressWarnings("deprecation")
//
//        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
//        return ip;
//    }
//
//    public class ClientClass extends Thread {
//
//        private Socket clientSocket;
//        private OutputStream out;
//        private InputStream in;
//
//        public void startDefaultConnection() {
//            try {
//                clientSocket = new Socket(IPaddress, 35000);
////                clientSocket = new Socket("192.168.0.10", 35000);
//
//                out = clientSocket.getOutputStream();
//                in = clientSocket.getInputStream();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void rpmCommand() {
//            try {
//
//                RPMCommand rpmCommand = new RPMCommand();
//                rpmCommand.run(in, out);
//                result = rpmCommand.getFormattedResult();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void throttleCommand() {
//            try {
//
//                ThrottlePositionCommand throttleCommand = new ThrottlePositionCommand();
//                throttleCommand.run(in, out);
//                result = throttleCommand.getFormattedResult();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void stopConnection() {
//            try {
//                in.close();
//                out.close();
//                clientSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public synchronized void start() {
//            super.start();
//        }
//
//        @Override
//        public void run() {
//            try {
//                clientSocket = new Socket(IPaddress, 35000);
////                clientSocket = new Socket("192.168.0.10", 35000);
//
//                out = clientSocket.getOutputStream();
//                in = clientSocket.getInputStream();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
