package com.example.camerinosports;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.media.MediaPlayer;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoSplash;
    private boolean loginAbierto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoSplash = findViewById(R.id.videoSplash);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_camerino);
        videoSplash.setVideoURI(videoUri);

        videoSplash.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoSplash.start();
            }
        });

        videoSplash.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                abrirLogin();
            }
        });

        videoSplash.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                abrirLogin();
                return true;
            }
        });

        // respaldo por si el video no finaliza en el emulador
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirLogin();
            }
        }, 5000);
    }

    private void abrirLogin() {
        if (loginAbierto) {
            return;
        }

        loginAbierto = true;
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
