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

import com.example.obdtest.commands.ObdCommand;
import com.example.obdtest.commands.control.ModuleVoltageCommand;
import com.example.obdtest.commands.control.TimingAdvanceCommand;
import com.example.obdtest.commands.control.VinCommand;
import com.example.obdtest.commands.engine.AbsoluteLoadCommand;
import com.example.obdtest.commands.engine.LoadCommand;
import com.example.obdtest.commands.engine.MassAirFlowCommand;
import com.example.obdtest.commands.engine.RPMCommand;
import com.example.obdtest.commands.engine.ThrottlePositionCommand;
import com.example.obdtest.commands.fuel.AirFuelRatioCommand;
import com.example.obdtest.commands.fuel.FindFuelTypeCommand;
import com.example.obdtest.commands.fuel.FuelLevelCommand;
import com.example.obdtest.commands.temperature.EngineCoolantTemperatureCommand;

import java.net.InetAddress;
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
    Button waterTempCommand;

    Button rpmCommand;
    Button timeAdvance;
    Button engineLoad;
    Button engineAbsLoad;

    Button maf;
    Button lambda;
    Button fuelType;
    Button fuelLevel;

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
        waterTempCommand = (Button) findViewById(R.id.waterTemp);

        rpmCommand = (Button) findViewById(R.id.rpm);
        timeAdvance = (Button) findViewById(R.id.timingAdvance);
        engineLoad = (Button) findViewById(R.id.engineLoad);
        engineAbsLoad = (Button) findViewById(R.id.absEngLoad);
        maf = (Button) findViewById(R.id.maf);
        lambda = (Button) findViewById(R.id.lambda);
        fuelType = (Button) findViewById(R.id.fuelType);
        fuelLevel = (Button) findViewById(R.id.fuelLevel);

        setOnClickListeners();

        VLinkConnect();
    }

    public void setOnClickListeners() {
        //wifiManager_enable.setOnClickListener(this);

//        wifiManager_scan.setOnClickListener(this);

        vinNoCommand.setOnClickListener(this);
        throttlePosCommand.setOnClickListener(this);
        currVoltageCommand.setOnClickListener(this);
        waterTempCommand.setOnClickListener(this);

        rpmCommand.setOnClickListener(this);
        timeAdvance.setOnClickListener(this);
        engineLoad.setOnClickListener(this);
        engineAbsLoad.setOnClickListener(this);

        maf.setOnClickListener(this);
        lambda.setOnClickListener(this);
        fuelType.setOnClickListener(this);
        fuelLevel.setOnClickListener(this);

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

        private void VLinkConnect() {
        wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = "V-Link";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        int inetId = wifiManager.addNetwork(wifiConfig);

        wifiManager.enableNetwork(inetId, true);
        wifiManager.reconnect();
    }
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
                        executeCommand(new VinCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.throttlePos:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new ThrottlePositionCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.currVoltage:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new ModuleVoltageCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.waterTemp:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new EngineCoolantTemperatureCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.rpm:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new RPMCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.timingAdvance:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new TimingAdvanceCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.engineLoad:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new LoadCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.absEngLoad:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new AbsoluteLoadCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.maf:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new MassAirFlowCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.lambda:
                    try {
                        if (task != null)
                            task.cancel(true);

                        executeCommand(new AirFuelRatioCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.fuelType:
                    try {
                        executeCommand(new FindFuelTypeCommand());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.fuelLevel:
                    try {
                        executeCommand(new FuelLevelCommand());
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
    }

    private void executeCommand(ObdCommand obdCommand) {
        task = new ClientClass(obdCommand, new ClientClass.ObdCommandResponse() {
            @Override
            public void getObdRawResponse(String response) {
                result_raw_text.setText(response);
            }

            @Override
            public void getObdFormattedResponse(String response) {
                result_text.setText(response);
            }
        }).execute();
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
