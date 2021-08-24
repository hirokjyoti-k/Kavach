package com.infosyialab.kavach;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.infosyialab.kavach.Setup.*;

public class MainActivity extends AppCompatActivity {

    private ToggleButton sos;
    private TextView btn_status;
    private Location location;
    private LocationManager locationManager;
    private boolean StopAppService = false, audioBtnState = true;
    private String txtphn1, txtphn2, txtphn3, txtmsg, msg;
    private int msgduration, PROGRESS_DAILOFG_FLAG = 0;
    private String[] Phonenumbers;
    private RecordAudio recordAudio;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startService(new Intent(this, KavachService.class));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Searching for GPS");
            progressDialog.setCancelable(false);
            progressDialog.show();
            PROGRESS_DAILOFG_FLAG = 1;
        }

        sos = (ToggleButton) findViewById(R.id.sosbtn);
        btn_status = (TextView) findViewById(R.id.btn_status);
        Phonenumbers = new String[3];
        recordAudio = new RecordAudio(this);
        laodData();

        sos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sos.isChecked()) {
                    if (txtphn1 == null) {
                        Toast.makeText(MainActivity.this, "Please enter a phone number first", Toast.LENGTH_LONG).show();
                        sos.setChecked(false);
                        return;
                    }
                    startService(new Intent(MainActivity.this, KavachService.class));
                    btn_status.setText("KAVACH ACTIVATED");
                    StopAppService = false;
                    AppServices appServices = new AppServices();
                    appServices.start();
                    if (audioBtnState == true) {
                        recordAudio.startRecording();
                    }
                } else {
                    stopService(new Intent(MainActivity.this, KavachService.class));
                    btn_status.setText("KAVACH DEACTIVATE");
                    StopAppService = true;
                    if (audioBtnState == true) {
                        recordAudio.stopRecording();
                    }
                }
            }
        });
    }

    private void laodData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        txtphn1 = sharedPreferences.getString(PHONE1, null);
        txtphn2 = sharedPreferences.getString(PHONE2, null);
        txtphn3 = sharedPreferences.getString(PHONE3, null);
        txtmsg = sharedPreferences.getString(MESSAGE, "Emergengy Alert !!! I want urgent help at this given location.");
        msgduration = sharedPreferences.getInt(DURATION, 0);
        audioBtnState = sharedPreferences.getBoolean(AUDIOSWITCH, true);

        //last saved location is found
        if (PROGRESS_DAILOFG_FLAG == 0) {
            msg = txtmsg + " Link: https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude();
        }
        Phonenumbers[0] = txtphn1;
        if (txtphn2 != null) {
            Phonenumbers[1] = txtphn2;
        }
        if (txtphn3 != null) {
            Phonenumbers[2] = txtphn3;
        }

        switch (msgduration) {
            case 0: msgduration = 180000; break;
            case 1: msgduration = 300000; break;
            case 2: msgduration = 600000; break;
            case 3: msgduration = 900000; break;
            default: msgduration = 180000;
        }
        //getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        boolean isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnable) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (PROGRESS_DAILOFG_FLAG == 1) {
                        PROGRESS_DAILOFG_FLAG = 0;
                        progressDialog.dismiss();
                    }
                    msg = txtmsg + " Link: https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(MainActivity.this, "Please Enable GPS", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

        } else {
            Toast.makeText(this, "Please Enable GPS", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            //finish();
        }
    }

    public void setup(View view) {
        if (sos.isChecked()) {
            Toast.makeText(this, "Can't Change Settings Now", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, Setup.class));
        finish();
    }


    public void info(View view) {
        if (sos.isChecked()) {
            Toast.makeText(this, "Can't Change Settings Now", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, About.class));
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    class AppServices extends Thread {
        @Override
        public void run() {
            while (true) {
                if (StopAppService) {
                    return;
                }
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    for (String number : Phonenumbers) {
                        smsManager.sendTextMessage(number, null, msg, null, null);
                    }
                } catch (Exception ex) { }
                try {
                    Thread.sleep(msgduration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}