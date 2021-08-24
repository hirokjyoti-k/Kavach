package com.infosyialab.kavach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Setup extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner duration;
    private EditText phoneone,phonetwo,phonethree,textmessage;
    private SwitchCompat audioButton;
    private Button saveButton;
    private String txtphn1,txtphn2,txtphn3,txtmsg;
    private int msgduration;
    boolean audioBtnState = true;

    public static final String SHARED_PREFS = "sharedprefs";
    public static final String PHONE1 = "phone1";
    public static final String PHONE2 = "phone2";
    public static final String PHONE3 = "phone3";
    public static final String MESSAGE = "message";
    public static final String AUDIOSWITCH = "audioswitch";
    public static final String DURATION = "duration";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        phoneone =(EditText) findViewById(R.id.phn1);
        phonetwo =(EditText) findViewById(R.id.phn2);
        phonethree =(EditText) findViewById(R.id.phn3);
        textmessage =(EditText) findViewById(R.id.textmsg);
        saveButton =(Button) findViewById(R.id.savebtn);
        audioButton =(SwitchCompat) findViewById(R.id.audioBtn);

        duration =(Spinner) findViewById(R.id.msgduration);
        duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                msgduration  = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        loadData();
        updateView();
    }

    private void saveData() {

        txtphn1 = phoneone.getText().toString().trim();
        txtphn2 = phonetwo.getText().toString().trim();
        txtphn3 = phonethree.getText().toString().trim();
        txtmsg = textmessage.getText().toString().trim();

        if(txtphn1.isEmpty()){
            phoneone.setError("Please Enter a Number");
            return;
        }

        audioBtnState = audioButton.isChecked();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PHONE1, txtphn1);
        editor.putString(PHONE2, txtphn2);
        editor.putString(PHONE3, txtphn3);
        editor.putString(MESSAGE, txtmsg);
        editor.putInt(DURATION, msgduration);
        editor.putBoolean(AUDIOSWITCH, audioBtnState);
        editor.apply();

        Toast.makeText(this, "Saved Succesfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Setup.this, MainActivity.class));
        finish();
    }

    public void loadData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        txtphn1 = sharedPreferences.getString(PHONE1,null);
        txtphn2 = sharedPreferences.getString(PHONE2,null);
        txtphn3 = sharedPreferences.getString(PHONE3,null);
        txtmsg = sharedPreferences.getString(MESSAGE,"Emergency Alert !!! I want urgent help at this given location.");
        msgduration = sharedPreferences.getInt(DURATION, 0);
        audioBtnState = sharedPreferences.getBoolean(AUDIOSWITCH, true);
    }

    public void updateView(){

        phoneone.setText(txtphn1);
        phonetwo.setText(txtphn2);
        phonethree.setText(txtphn3);
        textmessage.setText(txtmsg);
        duration.setSelection(msgduration);
        audioButton.setChecked(audioBtnState);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Setup.this, MainActivity.class));
        finish();
    }
}