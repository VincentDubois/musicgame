package fr.univartois.iutlens.mmi.web2.musicgame;

import android.media.JetPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }

    @Override
    protected void onStart() {
        super.onStart();
        audioPlayer = AudioPlayer.get(this);
        audioPlayer.loadById(this, R.raw.android_project);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        audioPlayer.play();
    }

    @Override
    protected void onPause() {
        super.onPause();

        audioPlayer.pause();
    }
}
