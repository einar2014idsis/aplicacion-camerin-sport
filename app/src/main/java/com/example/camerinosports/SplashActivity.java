package com.example.camerinosports;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoSplash = findViewById(R.id.videoSplash);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_camerino);
        videoSplash.setVideoURI(videoUri);

        videoSplash.setOnPreparedListener(mp -> videoSplash.start());

        videoSplash.setOnCompletionListener(mp -> abrirLogin());

        videoSplash.setOnErrorListener((mp, what, extra) -> {
            abrirLogin();
            return true;
        });
    }

    private void abrirLogin() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}