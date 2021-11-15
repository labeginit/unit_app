package com.example.smarthouseandroidclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tech.gusavila92.websocketclient.WebSocketClient;

import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
        }
    }

    private void inflateLampDevice(Lamp lamp) {
        View lampRow = getLayoutInflater().inflate(R.layout.lamp_row, null, false);

        ImageView lampRowImage = (ImageView) lampRow.findViewById(R.id.lampRowImage);
        TextView lampRowName = (TextView) lampRow.findViewById(R.id.lampRowName);
        Button lampRowButton = (Button) lampRow.findViewById(R.id.lampRowButton);
        buttons.put(lamp.getDeviceID(), lampRowButton);
        lampRowName.setText(lamp.getDeviceID());
        if (lamp.isOn()) {
            lampRowButton.setText("ON");
        } else if (!lamp.isOn()) {
            lampRowButton.setText("OFF");
        }
        lampRowButton.setOnClickListener(v -> {
            if (lampRowButton.getText().equals("ON")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + lamp.getDeviceID() + "', 'on':'false'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + lamp.getDeviceID() + "', 'on':'false'}");
            } else if (lampRowButton.getText().equals("OFF")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + lamp.getDeviceID() + "', 'on':'true'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + lamp.getDeviceID() + "', 'on':'true'}");
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
        fanRowName.setText(fan.getDeviceID());
        fanSlider.setValue(fan.getSpeed());

        fanSlider.addOnChangeListener((slider, value, fromUser) -> {
            Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + fan.getDeviceID() + "', 'speed':'" + value + "'}");
            webSocketClient.send("changeDeviceStatus={'_id':'" + fan.getDeviceID() + "', 'speed':'" + (int) value + "'}");

        });

        deviceLayout.addView(fanRow);
    }

    private void inflateCurtainDevice(Curtain curtain) {
        View curtainRow = getLayoutInflater().inflate(R.layout.curtain_row, null, false);

        ImageView curtainRowImage = (ImageView) curtainRow.findViewById(R.id.curtainRowImage);
        TextView curtainRowName = (TextView) curtainRow.findViewById(R.id.curtainRowName);
        Button curtainRowButton = (Button) curtainRow.findViewById(R.id.curtainRowButton);
        buttons.put(curtain.getDeviceID(), curtainRowButton);
        curtainRowName.setText(curtain.getDeviceID());
        if (curtain.isOpen()) {
            curtainRowButton.setText("OPEN");
        } else {
            curtainRowButton.setText("CLOSED");
        }
        curtainRowButton.setOnClickListener(v -> {
            if (curtainRowButton.getText().equals("OPEN")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + curtain.getDeviceID() + "', 'open':'" + false + "'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + curtain.getDeviceID() + "', 'open':'" + false + "'}");
            } else if (curtainRowButton.getText().equals("CLOSED")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + curtain.getDeviceID() + "', 'open':'" + true + "'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + curtain.getDeviceID() + "', 'open':'" + true + "'}");
            }
        });
        deviceLayout.addView(curtainRow);
    }

    private void inflateTemperatureSensorDevice(Thermometer thermometer) {
        View temperatureSensorRow = getLayoutInflater().inflate(R.layout.temperature_sensor_row, null, false);

        ImageView temperatureRowImage = (ImageView) temperatureSensorRow.findViewById(R.id.temperatureSensorImage);
        TextView temperatureSensorName = (TextView) temperatureSensorRow.findViewById(R.id.temperatureSensorName);
        TextView temperatureSensorTemperature = (TextView) temperatureSensorRow.findViewById(R.id.temperature);
        temperatureSensorName.setText(thermometer.getDeviceID());
        temperatureSensorTemperature.setText(Double.toString(thermometer.getTemperature()));

        deviceLayout.addView(temperatureSensorRow);
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
        } else if (!newCurtainStatus) { // New lamp status is false
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

}