package com.infosyialab.kavach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class About extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(About.this, MainActivity.class));
        finish();
    }

    public void privacy(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://infosyialab.in/res/Kavach/privacy.html")));
    }
}