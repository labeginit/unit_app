package com.example.smarthouseandroidclient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import Models.Alarm;
import tech.gusavila92.websocketclient.WebSocketClient;

import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import Models.Curtain;
import Models.Fan;
import Models.Lamp;
import Models.SmartHouse;
import Models.Thermometer;

public class MainActivity extends AppCompatActivity {
    private WebSocketClient webSocketClient;
    private SmartHouse smartHouse = SmartHouse.getInstance();
    private LinearLayout deviceLayout;
    private String errorTag = "Errors";
    private HashMap<String, Button> buttons = new HashMap<>();
    private Slider fanSliderGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        createWebSocketClient();
    }

    private void inflateDevices() {
        Log.d("Websocket", "inflate " + smartHouse.toString());
        if (smartHouse != null) {
            deviceLayout = findViewById(R.id.DeviceLayout);
            for (Lamp lamp : smartHouse.getLampList()) {
                inflateLampDevice(lamp);
            }
            for (Fan fan : smartHouse.getFanList()) {
                inflateFanDevice(fan);
            }
            for (Curtain curtain : smartHouse.getCurtainList()) {
                inflateCurtainDevice(curtain);
            }
            for (Thermometer thermometer : smartHouse.getTemperatureSensorList()) {
                inflateTemperatureSensorDevice(thermometer);
            }

            for (Alarm alarm : smartHouse.getAlarmList()) {
                inflateAlarmDevice(alarm);
            }
        }
    }

    private void inflateLampDevice(Lamp lamp) {
        View lampRow = getLayoutInflater().inflate(R.layout.lamp_row, null, false);

        ImageView lampRowImage = (ImageView) lampRow.findViewById(R.id.lampRowImage);
        TextView lampRowName = (TextView) lampRow.findViewById(R.id.lampRowName);
        Button lampRowButton = (Button) lampRow.findViewById(R.id.lampRowButton);
        buttons.put(lamp.get_id(), lampRowButton);
        lampRowName.setText(lamp.get_id());
        if (lamp.isStatus()) {
            lampRowButton.setText("ON");
        } else if (!lamp.isStatus()) {
            lampRowButton.setText("OFF");
        }
        lampRowButton.setOnClickListener(v -> {
            if (lampRowButton.getText().equals("ON")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + lamp.get_id() + "', 'status':'false'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + lamp.get_id() + "', 'status':'false'}");
            } else if (lampRowButton.getText().equals("OFF")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + lamp.get_id() + "', 'status':'true'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + lamp.get_id() + "', 'status':'true'}");
            }
        });
        deviceLayout.addView(lampRow);
    }

    private void inflateFanDevice(Fan fan) {
        View fanRow = getLayoutInflater().inflate(R.layout.fan_row, null, false);

        ImageView fanRowImage = (ImageView) fanRow.findViewById(R.id.fanRowImage);
        TextView fanRowName = (TextView) fanRow.findViewById(R.id.fanRowName);
        Slider fanSlider = (Slider) fanRow.findViewById(R.id.fanSpeedSlider);
        fanSliderGlobal = fanSlider;
        fanRowName.setText(fan.get_id());
        fanSlider.setValue(fan.getStatus());

        fanSlider.addOnChangeListener((slider, value, fromUser) -> {
            Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + fan.get_id() + "', 'status':'" + value + "'}");
            webSocketClient.send("changeDeviceStatus={'_id':'" + fan.get_id() + "', 'status':'" + (int) value + "'}");

        });

        deviceLayout.addView(fanRow);
    }

    private void inflateCurtainDevice(Curtain curtain) {
        View curtainRow = getLayoutInflater().inflate(R.layout.curtain_row, null, false);

        ImageView curtainRowImage = (ImageView) curtainRow.findViewById(R.id.curtainRowImage);
        TextView curtainRowName = (TextView) curtainRow.findViewById(R.id.curtainRowName);
        Button curtainRowButton = (Button) curtainRow.findViewById(R.id.curtainRowButton);
        buttons.put(curtain.get_id(), curtainRowButton);
        curtainRowName.setText(curtain.get_id());
        if (curtain.isStatus()) {
            curtainRowButton.setText("OPEN");
        } else {
            curtainRowButton.setText("CLOSED");
        }
        curtainRowButton.setOnClickListener(v -> {
            if (curtainRowButton.getText().equals("OPEN")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + curtain.get_id() + "', 'status':'" + false + "'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + curtain.get_id() + "', 'status':'" + false + "'}");
            } else if (curtainRowButton.getText().equals("CLOSED")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + curtain.get_id() + "', 'status':'" + true + "'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + curtain.get_id() + "', 'status':'" + true + "'}");
            }
        });
        deviceLayout.addView(curtainRow);
    }

    private void inflateTemperatureSensorDevice(Thermometer thermometer) {
        View temperatureSensorRow = getLayoutInflater().inflate(R.layout.temperature_sensor_row, null, false);

        ImageView temperatureRowImage = (ImageView) temperatureSensorRow.findViewById(R.id.temperatureSensorImage);
        TextView temperatureSensorName = (TextView) temperatureSensorRow.findViewById(R.id.temperatureSensorName);
        TextView temperatureSensorTemperature = (TextView) temperatureSensorRow.findViewById(R.id.temperature);
        temperatureSensorName.setText(thermometer.get_id());
        temperatureSensorTemperature.setText(Double.toString(thermometer.getStatus()));

        deviceLayout.addView(temperatureSensorRow);
    }

    private void inflateAlarmDevice(Alarm alarm) {
        View alarmRow = getLayoutInflater().inflate(R.layout.alarm_row, null, false);

        ImageView alarmRowImage = (ImageView) alarmRow.findViewById(R.id.alarmRowImage);
        TextView alarmRowName = (TextView) alarmRow.findViewById(R.id.alarmRowName);
        Button alarmRowButton = (Button) alarmRow.findViewById(R.id.alarmRowButton);
        buttons.put(alarm.get_id(), alarmRowButton);
        alarmRowName.setText(alarm.get_id());
        if (alarm.getStatus() == 0) {
            alarmRowButton.setText("OFF");
        } else if (alarm.getStatus() == 1 || alarm.getStatus() == 2) { // If the alarm is going off it must be ON in the first place
            alarmRowButton.setText("ON");
        }
        alarmRowButton.setOnClickListener(v -> {
            if (alarmRowButton.getText().equals("ON")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + alarm.get_id() + "', 'status':' " + 0 + "'}"); // If the alarm is ON we send 0 to turn it OFF
                webSocketClient.send("changeDeviceStatus={'_id':'" + alarm.get_id() + "', 'status':'" + 0 + "'}");
            } else if (alarmRowButton.getText().equals("OFF")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + alarm.get_id() + "', 'status':'" + 1 + "'}"); // If the alarm is OFF we send 1 to turn it ON
                webSocketClient.send("changeDeviceStatus={'_id':'" + alarm.get_id() + "', 'status':'" + 1 + "'}");
            }
        });
        deviceLayout.addView(alarmRow);
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://ro01.beginit.se:1337/websocket");
            // ws://ro01.beginit.se:1337/websocket
            // ws://192.168.1.14:8080/websocket
        } catch (URISyntaxException e) {
            Log.e(errorTag, "Failed to create URI");
            e.printStackTrace();
            return;
        }
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Websocket session established!");
                webSocketClient.send("getDevices");
            }

            @Override
            public void onTextReceived(String response) {
                Log.d("Websocket", "Message recieved from server: " + response);
                String[] parts = response.split("=");
                String operation = parts[0];
                String payload = parts[1];

                switch (operation) {
                    case "getDevices":
                        Gson gson = new Gson();
                        smartHouse = gson.fromJson(payload, SmartHouse.class);
                        Log.d("Websocket", "SmartHouse now loaded!");
                        runOnUiThread(() -> {   // Task the UI thread to inflate the devices
                            inflateDevices();
                            Toast.makeText(getApplicationContext(), "Devices are now loaded from server.", Toast.LENGTH_SHORT).show();
                        });
                        break;

                    case "changeDeviceStatus":
                        Log.d("Websocket", "Payload: " + payload + " ");

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(payload);
                            if (jsonObject.get("operation").toString().equals("success")) { // If the status has been successfully changed
                                if (jsonObject.get("device").toString().equals("lamp")) { // A lamp has changed status
                                   updateLampInGUI(jsonObject);
                                } else if (jsonObject.get("device").toString().equals("curtain")) { // A curtain has changed status
                                   updateCurtainInGUI(jsonObject);
                                } else if (jsonObject.get("device").toString().equals("fan")) {
                                   updateFanInGUI(jsonObject);
                                } else if (jsonObject.get("device").toString().equals("alarm")) {
                                    updateAlarmInGUI(jsonObject);
                                }
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Your operation to change status failed!", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (JSONException e) {
                            Log.d("Websocket", "JSON error!");
                        }

                        break;

                    default:
                        runOnUiThread(() -> {   // Display the message by using the GUI thread
                            Toast.makeText(getApplicationContext(), "Unknown operation", Toast.LENGTH_SHORT).show();
                        });
                        break;
                }


            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {
                Log.e(errorTag, e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    private void updateLampInGUI(JSONObject jsonObject) throws JSONException {
        String lampID = jsonObject.get("_id").toString();
        boolean newLampStatus = Boolean.parseBoolean(jsonObject.get("option").toString());
        Log.d("Websocket", "Lamp name: " + lampID + " " + "New status: " + newLampStatus);
        if (newLampStatus) { // true
            runOnUiThread(() -> {   // Display the message by using the GUI thread
                buttons.get(lampID).setText("ON");
            });
        } else if (!newLampStatus) { // New lamp status is false
            runOnUiThread(() -> {
                buttons.get(lampID).setText("OFF");
            });
        }
    }

    private void updateCurtainInGUI(JSONObject jsonObject) throws JSONException {
        String curtainID = jsonObject.get("_id").toString();
        boolean newCurtainStatus = Boolean.parseBoolean(jsonObject.get("option").toString());
        Log.d("Websocket", "Curtain name: " + curtainID + " " + "New status: " + newCurtainStatus);

        if (newCurtainStatus) { // true
            runOnUiThread(() -> {   // Display the message by using the GUI thread
                buttons.get(curtainID).setText("OPEN");
            });
        } else if (!newCurtainStatus) { // New status is false
            runOnUiThread(() -> {
                buttons.get(curtainID).setText("CLOSED");
            });
        }
    }

    private void updateFanInGUI(JSONObject jsonObject) throws JSONException {
        String fanID = jsonObject.get("_id").toString();
        int newFanSpeed = Integer.parseInt(jsonObject.get("option").toString());
        Log.d("Websocket", "Fan name: " + fanID + " " + "New status: " + newFanSpeed);

        runOnUiThread(() -> {
            fanSliderGlobal.setValue(newFanSpeed);
        });
    }
    // TODO updateAlarmInGUI isn't tested as server isn't broadcasting at the time of implementation
    private void updateAlarmInGUI(JSONObject jsonObject) throws JSONException {
        String alarmID = jsonObject.get("_id").toString();
        int newAlarmStatus = Integer.parseInt(jsonObject.get("option").toString()); // Read in the status sent from the server
        Log.d("Websocket", "Alarm name: " + alarmID + " " + "New status: " + newAlarmStatus);

        if (newAlarmStatus == 0) { // The new status of the Alarm is OFF
            runOnUiThread(() -> {   // Display the message by using the GUI thread
                buttons.get(alarmID).setText("OFF");
            });
        } else if (newAlarmStatus == 1) { // The new status of the Alarm is ON
            runOnUiThread(() -> {
                buttons.get(alarmID).setText("ON");
            });
            // TODO TEST BELOW CODE
        } else if (newAlarmStatus == 2) { // We get information about the alarm being triggered
            runOnUiThread(() -> {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Alarm triggered!")
                        .setMessage("Would you like to call security?")

                        .setPositiveButton(android.R.string.yes, (dialog, which) -> { // The user wants to call security
                            webSocketClient.send("changeDeviceStatus={'_id':'" + alarmID + "', 'status':'" + 0 + "'}"); // The alarm is turned off, then security can be called
                            Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + alarmID + "', 'status':'" + 0 + "'}");
                            String phone = "+911"; // TODO Find a better number to call perhaps
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                            startActivity(intent);                        })

                        .setNegativeButton(android.R.string.no, (dialog, which) -> { // The user doesn't want to call security
                            webSocketClient.send("changeDeviceStatus={'_id':'" + alarmID + "', 'status':'" + 0 + "'}"); // The alarm is turned off.
                            Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'\" + alarmID + \"', 'status':'\" + 0 + \"'}");
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            });
        }
    }

}