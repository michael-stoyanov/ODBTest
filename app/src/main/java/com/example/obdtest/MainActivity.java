package com.example.obdtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obdtest.commands.control.ModuleVoltageCommand;
import com.example.obdtest.commands.control.VinCommand;
import com.example.obdtest.commands.engine.OilTempCommand;
import com.example.obdtest.commands.engine.RPMCommand;
import com.example.obdtest.commands.engine.ThrottlePositionCommand;
import com.example.obdtest.commands.temperature.EngineCoolantTemperatureCommand;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    WifiManager wifiManager;
    WifiConfiguration wifiConfig;

    //    ListView networks;
    TextView result_text;
    TextView result_raw_text;

    //Button wifiManager_enable;
    //Button wifiManager_scan;

    Button vinNoCommand;
    Button throttlePosCommand;

    Button currVoltageCommand;
    Button oilCommand;

    Button rpmCommand;

    //    Button vLinkConnect;

    List<ScanResult> scanResultList;

    AsyncTask task;

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            scanResultList = wifiManager.getScanResults();
            String deviceArray[] = new String[scanResultList.size()];

            for (int i = 0; i < scanResultList.size(); i++) {
                deviceArray[i] = scanResultList.get(i).SSID;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceArray);

//            networks.setAdapter(adapter);
//            networks.setVisibility(View.VISIBLE);

            unregisterReceiver(this);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        result_text = (TextView) findViewById(R.id.result_text);
        result_raw_text = (TextView) findViewById(R.id.result_raw_text);
//        networks = (ListView) findViewById(R.id.wifi_networks_listView);
//        networks.setVisibility(View.INVISIBLE);

//        wifiManager_scan = (Button) findViewById(R.id.wifi_search);
//        wifiManager_enable = (Button) findViewById(R.id.wifi_enable);

        vinNoCommand = (Button) findViewById(R.id.vinNo);
        throttlePosCommand = (Button) findViewById(R.id.throttlePos);

        currVoltageCommand = (Button) findViewById(R.id.currVoltage);
        oilCommand = (Button) findViewById(R.id.oilTemp);

        rpmCommand = (Button) findViewById(R.id.rpm);

//        vLinkConnect = (Button) findViewById(R.id.VLinkConnect);
        setOnClickListeners();

        task = new ClientClass();
    }

    public void setOnClickListeners() {
        //wifiManager_enable.setOnClickListener(this);

//        wifiManager_scan.setOnClickListener(this);

        rpmCommand.setOnClickListener(this);
        vinNoCommand.setOnClickListener(this);

        throttlePosCommand.setOnClickListener(this);
        currVoltageCommand.setOnClickListener(this);

        oilCommand.setOnClickListener(this);

//        networks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ScanResult device = scanResultList.get(position);
//
//                wifiConfig = new WifiConfiguration();
//                wifiConfig.SSID = String.format("\"%s\"", device.SSID);
//
//                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//
//                int inetId = wifiManager.addNetwork(wifiConfig);
//
//                wifiManager.disconnect();
//                wifiManager.enableNetwork(inetId, true);
//                wifiManager.reconnect();
//
//                networks.setVisibility(View.INVISIBLE);
//            }
//        });
    }

    //    private void VLinkConnect() {
//        wifiConfig = new WifiConfiguration();
//
//        wifiConfig.SSID = "V-Link";
//        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//
//        int inetId = wifiManager.addNetwork(wifiConfig);
//
//        wifiManager.disconnect();
//        wifiManager.enableNetwork(inetId, true);
//        wifiManager.reconnect();
//    }
//
//    @SuppressWarnings("deprecation")
//    public void scanWifi() {
//        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//
//        wifiManager.startScan();
//        Toast.makeText(this, "Scanning .....", Toast.LENGTH_LONG).show();
//    }
//        IPaddress = intToInetAddress(wifiManager.getDhcpInfo().serverAddress).getHostAddress();
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
                case R.id.vinNo:
                    try {
                        task = new ClientClass(new VinCommand(), new ClientClass.ObdCommandResponse() {
                            @Override
                            public void getObdFormattedResponse(String response) {
                                result_text.setText(response);
                            }

                            @Override
                            public void getObdRawResponse(String response) {
                                result_raw_text.setText(response);
                            }
                        }).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
//                case R.id.sendVinNoCommand:
//                    break;
                case R.id.throttlePos:
                    try {
                        task = new ClientClass(new ThrottlePositionCommand(), new ClientClass.ObdCommandResponse() {
                            @Override
                            public void getObdFormattedResponse(String response) {
                                result_text.setText(response);
                            }

                            @Override
                            public void getObdRawResponse(String response) {
                                result_raw_text.setText(response);
                            }
                        }).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.currVoltage:
                    try {
                        task = new ClientClass(new ModuleVoltageCommand(), new ClientClass.ObdCommandResponse() {
                            @Override
                            public void getObdFormattedResponse(String response) {
                                result_text.setText(response);
                            }

                            @Override
                            public void getObdRawResponse(String response) {
                                result_raw_text.setText(response);
                            }
                        }).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.oilTemp:
                    try {
                        task = new ClientClass(new EngineCoolantTemperatureCommand(), new ClientClass.ObdCommandResponse() {
                            @Override
                            public void getObdFormattedResponse(String response) {
                                result_text.setText(response);
                            }

                            @Override
                            public void getObdRawResponse(String response) {
                                result_raw_text.setText(response);
                            }
                        }).execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.rpm:
                    try {
                        task = new ClientClass(new RPMCommand(), new ClientClass.ObdCommandResponse() {
                            @Override
                            public void getObdRawResponse(String response) {
                                result_raw_text.setText(response);
                            }

                            @Override
                            public void getObdFormattedResponse(String response) {
                                result_text.setText(response);
                            }
                        }).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong! Better luck next time!", Toast.LENGTH_SHORT).show();
        }

        //task.cancel(true);
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
}
