package com.example.obdtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obdtest.commands.control.VinCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    LogWriter logWriter;

    String TAG = "OBD test";

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


    @SuppressWarnings("deprecation")
    public void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
        Toast.makeText(this, "Scanning .....", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View btn) {
        ClientClass client;
        VinCommand vinCommand = new VinCommand();
//        final Socket clientSocket;
//        final OutputStream out;
//        final InputStream in;

        try {
            switch (btn.getId()) {
//                case R.id.wifi_enable:
//                    if (!wifiManager.isWifiEnabled()) {
//                        Toast.makeText(getApplicationContext(), "Wifi    is disabled..making it enabled", Toast.LENGTH_LONG).show();
//                        wifiManager.setWifiEnabled(true);
//                    }
//                    break;
                case R.id.wifi_search:
                    networks = (ListView) findViewById(R.id.wifi_networks_listView);
                    scanWifi();
                    break;
                case R.id.VLinkConnect:
                    VLinkConnect();
                    break;
                case R.id.getMyIpAddress:
                    IPaddress = GetDeviceIpWiFiData();
                    result_text.setText(IPaddress);
                    break;
                case R.id.getHotspotIpAddress:
                    IPaddress = intToInetAddress(wifiManager.getDhcpInfo().serverAddress).getHostAddress();
                    result_text.setText(IPaddress);
                    break;
                case R.id.sendVinNoCommand:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {

                                Socket clientSocket = new Socket(IPaddress, 35000);

                                OutputStream out = clientSocket.getOutputStream();
                                InputStream in = clientSocket.getInputStream();

                                VinCommand vinCommand = new VinCommand();

                                vinCommand.run(in, out);

                                Toast.makeText(getApplicationContext(), vinCommand.getResult(), Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
//
//                    client.stopConnection();

                    /*
                    client = new ClientClass();
                    client.start();
                    client.startDefaultConnection();
*/


                    break;
                case R.id.hardcodedVin:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {

                                Socket clientSocket = new Socket("192.168.0.10", 35000);

                                OutputStream out = clientSocket.getOutputStream();
                                InputStream in = clientSocket.getInputStream();

                                VinCommand vinCommand = new VinCommand();

                                vinCommand.run(in, out);

                                Toast.makeText(getApplicationContext(), vinCommand.getResult(), Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Better luck next time!", Toast.LENGTH_SHORT).show();
        }
    }

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

    public String GetDeviceIpWiFiData() {
        @SuppressWarnings("deprecation")

        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ip;
    }

    public class ClientClass extends Thread {

        private Socket clientSocket;
        private OutputStream out;
        private InputStream in;

        public void startDefaultConnection() {
            try {
                clientSocket = new Socket("192.168.0.10", 35000);

                out = clientSocket.getOutputStream();
                in = clientSocket.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void startConnection(String ip, int port) {
            try {
                clientSocket = new Socket(ip, port);

                out = clientSocket.getOutputStream();
                in = clientSocket.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        public String sendMessage(String msg) {
//            //out.write(msg.getBytes());
//            String resp = null;
//            try {
//                resp = in.readLine();
//                result_text.setText(resp);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return resp;
//        }

        public void stopConnection() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
