package com.example.smarthouseandroidclient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import Models.Alarm;
import Models.Heater;
import tech.gusavila92.websocketclient.WebSocketClient;

import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import Models.Curtain;
import Models.Fan;
import Models.Lamp;
import Models.SmartHouse;
import Models.Thermometer;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "Alarm notification";
    private WebSocketClient webSocketClient;
    private SmartHouse smartHouse = SmartHouse.getInstance();
    private LinearLayout deviceLayout;
    private String errorTag = "Errors";
    private HashMap<String, Button> buttons = new HashMap<>(); // Hashmap storing all buttons in the GUI
    private HashMap<String, TextView> textViews = new HashMap<>(); // Hashmap containing all textviews, such as the ones for showing temperature for thermometers
    private HashMap<String, View> deviceViews = new HashMap<>();
    private Slider fanSliderGlobal;
    private LifeCycle lifeCycle;
    private Uri notificationSound;

    private enum LifeCycle {
        RESUMED,
        PAUSED,
        STOPPED,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        createNotificationChannel();
        createWebSocketClient();
        notificationSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.air_horn); // Sets the notification sound to air_horn.mp3 file in raw folder
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifeCycle = LifeCycle.RESUMED;
        Log.d("Websocket", "Application resumed");
    }

    @Override
    protected void onDestroy() {
        Log.d("Websocket", "Application destroyed");
        super.onDestroy();
        webSocketClient.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifeCycle = LifeCycle.PAUSED;
        //webSocketClient.close();
        Log.d("Websocket", "Application paused");
    }

    @Override
    protected void onStop() {
        lifeCycle = LifeCycle.STOPPED;
        super.onStop();
        //webSocketClient.close();
        Log.d("Websocket", "Application stopped");
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
            for (Thermometer thermometer : smartHouse.getThermometerList()) {
                inflateThermometerDevice(thermometer);
            }
            for (Heater heater : smartHouse.getHeaterList()) {
                inflateHeaterDevice(heater);
            }
            for (Alarm alarm : smartHouse.getAlarmList()) {
                inflateAlarmDevice(alarm);
            }

        }
        inflateAddNewDevice();
    }

    private void inflateAddNewDevice() {
        @SuppressLint("InflateParams") View newDeviceRow = getLayoutInflater().inflate(R.layout.new_device_row, null, false);

        ImageView newDeviceRowImage = (ImageView) newDeviceRow.findViewById(R.id.newDeviceRowImage);
        TextView newDeviceRowName = (TextView) newDeviceRow.findViewById(R.id.newDeviceRowName);
        Button newDeviceRowButton = (Button) newDeviceRow.findViewById(R.id.newDeviceRowButton);
        buttons.put(null, newDeviceRowButton);

        newDeviceRowButton.setOnClickListener(v -> {

            EditText inputField = new EditText(this); // Edittext displayed in the dialog later
            inputField.setTextSize(22);
            inputField.setTextColor(Color.WHITE);
            inputField.setHintTextColor(Color.GRAY);
            inputField.setHint("Enter device ID here:");
            inputField.setPadding(100, 40, 100, 40);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            String[] deviceList = {"Lamp", "Fan", "Thermometer", "Alarm"};
            final int[] selectedDevice = {0};
            builder.setTitle("Add new device")
                    .setSingleChoiceItems(deviceList, selectedDevice[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedDevice[0] = which;
                        }
                    })
                    .setView(inputField)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!inputField.getText().toString().equals("") && inputField.getText().length() > 3) { // Make sure inputfield contains some kind of ID.
                                runOnUiThread(() -> {   // Task the UI thread to inflate the devices
                                    webSocketClient.send("addNewDevice={\"_id\":\"" + inputField.getText() + "\",\"device\":\"" + deviceList[selectedDevice[0]].toLowerCase() + "\"}");
                                    Log.d("Websocket", "Command sent to server: " + "addNewDevice={\"_id\":\"" + inputField.getText() + "\",\"device\":\"" + deviceList[selectedDevice[0]].toLowerCase() + "\"}");
                                });
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    })
                    .show();
        });
        deviceLayout.addView(newDeviceRow);
    }

    private void inflateHeaterDevice(Heater heater) {
        View heaterRow = getLayoutInflater().inflate(R.layout.lamp_row, null, false);

        ImageView heaterRowImage = (ImageView) heaterRow.findViewById(R.id.heaterRowImage);
        TextView heaterRowName = (TextView) heaterRow.findViewById(R.id.heaterRowName);
        Button heaterRowButton = (Button) heaterRow.findViewById(R.id.heaterRowButton);
        buttons.put(heater.get_id(), heaterRowButton);
        heaterRowName.setText(heater.get_id());
        if (heater.getStatus()) {
            heaterRowButton.setText("ON");
        } else if (!heater.getStatus()) {
            heaterRowButton.setText("OFF");
        }
        heaterRowButton.setOnClickListener(v -> {
            if (heaterRowButton.getText().equals("ON")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + heater.get_id() + "', 'status':'false'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + heater.get_id() + "', 'status':'false'}");
            } else if (heaterRowButton.getText().equals("OFF")) {
                Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + heater.get_id() + "', 'status':'true'}");
                webSocketClient.send("changeDeviceStatus={'_id':'" + heater.get_id() + "', 'status':'true'}");
            }
        });

        heaterRow.setOnLongClickListener(v -> { // Listens if the row is long-pressed
            runOnUiThread(() -> removeDeviceAlert(heater.get_id(), "heater"));
            return true;
        });

        deviceLayout.addView(heaterRow);
        deviceViews.put(heater.get_id(), heaterRow);
    }

    private void inflateLampDevice(Lamp lamp) {
        View lampRow = getLayoutInflater().inflate(R.layout.lamp_row, null, false);

        ImageView lampRowImage = (ImageView) lampRow.findViewById(R.id.lampRowImage);
        TextView lampRowName = (TextView) lampRow.findViewById(R.id.lampRowName);
        Button lampRowButton = (Button) lampRow.findViewById(R.id.lampRowButton);
        buttons.put(lamp.get_id(), lampRowButton);
        lampRowName.setText(lamp.get_id());
        if (lamp.getStatus()) {
            lampRowButton.setText("ON");
        } else if (!lamp.getStatus()) {
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

        lampRow.setOnLongClickListener(v -> { // Listens if the row is long-pressed
            runOnUiThread(() -> removeDeviceAlert(lamp.get_id(), "lamp"));
            return true;
        });

        deviceLayout.addView(lampRow);
        deviceViews.put(lamp.get_id(), lampRow);
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

        fanRow.setOnLongClickListener(v -> { // Listens if the row is long-pressed
            runOnUiThread(() -> removeDeviceAlert(fan.get_id(), "fan"));
            return true;
        });

        deviceLayout.addView(fanRow);
        deviceViews.put(fan.get_id(), fanRow);
    }

    private void inflateCurtainDevice(Curtain curtain) {
        View curtainRow = getLayoutInflater().inflate(R.layout.curtain_row, null, false);

        ImageView curtainRowImage = (ImageView) curtainRow.findViewById(R.id.curtainRowImage);
        TextView curtainRowName = (TextView) curtainRow.findViewById(R.id.curtainRowName);
        Button curtainRowButton = (Button) curtainRow.findViewById(R.id.curtainRowButton);
        buttons.put(curtain.get_id(), curtainRowButton);
        curtainRowName.setText(curtain.get_id());
        if (curtain.getStatus()) {
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

        curtainRow.setOnLongClickListener(v -> { // Listens if the row is long-pressed
            runOnUiThread(() -> removeDeviceAlert(curtain.get_id(), "curtain"));
            return true;
        });

        deviceLayout.addView(curtainRow);
        deviceViews.put(curtain.get_id(), curtainRow);
    }

    private void inflateThermometerDevice(Thermometer thermometer) {
        View thermometerRow = getLayoutInflater().inflate(R.layout.temperature_sensor_row, null, false);

        ImageView temperatureRowImage = (ImageView) thermometerRow.findViewById(R.id.temperatureSensorImage);
        TextView temperatureSensorName = (TextView) thermometerRow.findViewById(R.id.temperatureSensorName);
        TextView temperatureSensorTemperature = (TextView) thermometerRow.findViewById(R.id.temperature);
        temperatureSensorName.setText(thermometer.get_id());
        temperatureSensorTemperature.setText(Double.toString(thermometer.getStatus()));
        textViews.put(thermometer.get_id(), temperatureSensorTemperature);

        thermometerRow.setOnLongClickListener(v -> { // Listens if the row is long-pressed
            runOnUiThread(() -> removeDeviceAlert(thermometer.get_id(), "thermometer"));
            return true;
        });

        deviceLayout.addView(thermometerRow);
        deviceViews.put(thermometer.get_id(), thermometerRow);
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
        } else if (alarm.getStatus() == 1) { // If the alarm is going off it must be ON in the first place
            alarmRowButton.setText("ON");
        } else if (alarm.getStatus() == 2) {
            alarmRowButton.setText("ON");
            displayAlarmAlert(alarm.get_id());
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

        alarmRow.setOnLongClickListener(v -> { // Listens if the row is long-pressed
            runOnUiThread(() -> removeDeviceAlert(alarm.get_id(), "alarm"));
            return true;
        });

        deviceLayout.addView(alarmRow);
        deviceViews.put(alarm.get_id(), alarmRow);
    }

    private void removeDeviceAlert(String deviceId, String deviceType) { // Where the remove request is sent to server
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Removing " + deviceId + " !")
                .setMessage("Are you sure you want to remove the device?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> { // The user wants to remove the device
                    webSocketClient.send("removeDevice={\"_id\":\"" + deviceId + "\",\"device\":\"" + deviceType + "\"}");
                    Log.d("Websocket", "Command sent to server: " + "removeDevice={_id:" + deviceId + ",device:" + deviceType + "}");
                })

                .setNegativeButton(android.R.string.no, (dialog, which) -> { // The user doesn't want to remove device
                    // Do nothing
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void createWebSocketClient() {
        URI uri;
        try {
//            uri = new URI("ws:///ro01.beginit.se:1337/websocket");
            uri = new URI("ws://ro01.beginit.se:1337/websocket");
            // ws://ro01.beginit.se:1337/websocket Lillia server
            // ws://192.168.1.14:8080/websocket
            // ws://172.20.10.7:8080/websocket
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
                    case "getDevices": // Handle getDevices response
                        handleGetDevicesResponse(payload);
                        break;

                    case "changeDeviceStatus": // Handle changeDeviceStatus response
                        handleChangeDeviceStatusResponse(payload);
                        break;

                    case "changeDeviceStatus2Device":
                        // This is ignored for Units clients, but handled by devices
                        break;

                    case "addNewDevice": // We recieve a broadcast that a new device has been successfully added
                        handleAddNewDeviceResponse(payload);
                        break;

                    case "removeDevice": // We receive a broadcast that a device has been successfully removed
                        handleRemoveDeviceResponse(payload);
                        break;

                    default:
                        runOnUiThread(() -> {   // Display the message by using the GUI thread
                            Toast.makeText(getApplicationContext(), "Unknown operation", Toast.LENGTH_SHORT).show(); // Server sends an operation that isn't supported in protocol / faulty message.
                        });
                        break;
                }
            }

            private void handleGetDevicesResponse(String payload) {
                Gson gson = new Gson();
                smartHouse = gson.fromJson(payload, SmartHouse.class);
                Log.d("Websocket", "SmartHouse now loaded!");
                runOnUiThread(() -> {   // Task the UI thread to inflate the devices
                    inflateDevices();
                    Toast.makeText(getApplicationContext(), "Devices are now loaded from server.", Toast.LENGTH_SHORT).show();
                });
            }

            private void handleChangeDeviceStatusResponse(String payload) {
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
                        } else if (jsonObject.get("device").toString().equals("thermometer")) {
                            updateThermometerInGUI(jsonObject);
                        }
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Your operation to change status failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    Log.d("Websocket", e.getMessage());
                }

            }

            private void handleAddNewDeviceResponse(String payload) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(payload);
                    if (jsonObject.get("operation").toString().equals("success")) {
                        String deviceId = jsonObject.get("_id").toString();

                        if (jsonObject.get("device").toString().equals("lamp")) { // A lamp has been successfully added
                            Log.d("Websocket", "Lamp added");
                            smartHouse.getLampList().add(new Lamp(deviceId, Boolean.parseBoolean(jsonObject.get("status").toString())));
                        } else if (jsonObject.get("device").toString().equals("curtain")) {
                            smartHouse.getCurtainList().add(new Curtain(deviceId, Boolean.parseBoolean(jsonObject.get("status").toString())));
                        } else if (jsonObject.get("device").toString().equals("fan")) {
                            smartHouse.getFanList().add(new Fan(deviceId, Integer.parseInt(jsonObject.get("status").toString())));
                        } else if (jsonObject.get("device").toString().equals("alarm")) {
                            smartHouse.getAlarmList().add(new Alarm(deviceId, Integer.parseInt(jsonObject.get("status").toString())));
                        } else if (jsonObject.get("device").toString().equals("thermometer")) {
                            smartHouse.getThermometerList().add(new Thermometer(deviceId, Double.parseDouble(jsonObject.get("status").toString())));
                        }

                        Log.d("Websocket", jsonObject.get("_id").toString() + " has been added!");
                        runOnUiThread(() -> { // Remove and re-inflate the devices to have them sorted in device order
                            deviceLayout.removeAllViews();
                            inflateDevices();
                        });

                    } else if (jsonObject.get("operation").toString().equals("failed")) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Failed to add device", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void handleRemoveDeviceResponse(String payload) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(payload);
                    if (jsonObject.get("operation").toString().equals("success")) {
                        String deviceId = jsonObject.getString("_id");

                        if (jsonObject.get("device").toString().equals("lamp")) { // A lamp has been successfully added
                            smartHouse.getLampList().removeIf(lamp -> lamp.get_id().equals(deviceId));
                        } else if (jsonObject.get("device").toString().equals("curtain")) {
                            smartHouse.getCurtainList().removeIf(curtain -> curtain.get_id().equals(deviceId));
                        } else if (jsonObject.get("device").toString().equals("fan")) {
                            smartHouse.getFanList().removeIf(fan -> fan.get_id().equals(deviceId));
                        } else if (jsonObject.get("device").toString().equals("alarm")) {
                            smartHouse.getAlarmList().removeIf(alarm -> alarm.get_id().equals(deviceId));
                        } else if (jsonObject.get("device").toString().equals("thermometer")) {
                            smartHouse.getThermometerList().removeIf(thermometer -> thermometer.get_id().equals(deviceId));
                        }
                        runOnUiThread(() -> { // Remove and re-inflate the devices to have them sorted in device order
                            deviceLayout.removeAllViews();
                            inflateDevices();
                        });
                        Log.d("Websocket", jsonObject.getString("_id") + " has been removed.");
                    } else if (jsonObject.get("operation").toString().equals("failed")) {
                        runOnUiThread(() -> {   // Display the message by using the GUI thread
                            Toast.makeText(getApplicationContext(), "Failed to remove device", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                Log.d("Websocket", "Closed ");
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    private void updateLampInGUI(JSONObject jsonObject) throws JSONException {
        String lampID = jsonObject.get("_id").toString();
        boolean newLampStatus = Boolean.parseBoolean(jsonObject.get("status").toString());
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
        boolean newCurtainStatus = Boolean.parseBoolean(jsonObject.get("status").toString());
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
        int newFanSpeed = Integer.parseInt(jsonObject.get("status").toString());
        Log.d("Websocket", "Fan name: " + fanID + " " + "New status: " + newFanSpeed);

        runOnUiThread(() -> {
            fanSliderGlobal.setValue(newFanSpeed);
        });
    }

    private void updateThermometerInGUI(JSONObject jsonObject) throws JSONException {
        String thermometerID = jsonObject.get("_id").toString();
        double newTemperature = Double.parseDouble(jsonObject.get("status").toString());
        Log.d("Websocket", "Thermometer name: " + thermometerID + " New status: " + newTemperature);

        runOnUiThread(() -> {
            textViews.get(thermometerID).setText(String.valueOf(newTemperature)); // Retrieve the correct textview and update temperature
        });

    }

    // TODO updateAlarmInGUI isn't tested as server isn't broadcasting at the time of implementation
    private void updateAlarmInGUI(JSONObject jsonObject) throws JSONException {
        String alarmID = jsonObject.get("_id").toString();
        int newAlarmStatus = Integer.parseInt(jsonObject.get("status").toString()); // Read in the status sent from the server
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
                if (lifeCycle == LifeCycle.RESUMED) {
                    displayAlarmAlert(alarmID);
                } else if (lifeCycle == LifeCycle.PAUSED || lifeCycle == LifeCycle.STOPPED) {
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound); // Use the RingtoneManager to play the airhorn sound when the notification is triggered.
                    r.play();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)   // The code below builds a notification that informs the user that the alarm is triggered, even if app is in stopped / paused.
                            .setSmallIcon(R.drawable.alarm_icon)
                            .setContentTitle("Alarm going off!")
                            .setContentText("The alarm is triggered in your house!")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                    managerCompat.notify(1, builder.build());
                }
            });
        }
    }

    private void displayAlarmAlert(String alarmID) {
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert) // shows an alert if the application is open
                .setTitle("Alarm triggered!")
                .setMessage("Would you like to call security?")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> { // The user wants to call security
                    webSocketClient.send("changeDeviceStatus={'_id':'" + alarmID + "', 'status':'" + 0 + "'}"); // The alarm is turned off, then security can be called
                    Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'" + alarmID + "', 'status':'" + 0 + "'}");
                    String phone = "+911"; // TODO Find a better number to call perhaps
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                })

                .setNegativeButton(android.R.string.no, (dialog, which) -> { // The user doesn't want to call security
                    webSocketClient.send("changeDeviceStatus={'_id':'" + alarmID + "', 'status':'" + 0 + "'}"); // The alarm is turned off.
                    Log.d("Websocket", "Command sent to server: changeDeviceStatus={'_id':'\" + alarmID + \"', 'status':'\" + 0 + \"'}");
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "Channel for creating alarm notifications to the user.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}