package fr.univartois.iutlens.mmi.web2.musicgame;

import android.media.JetPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            JetPlayer jetPlayer = JetPlayer.getJetPlayer();
            jetPlayer.loadJetFile(getResources().getAssets().openFd("test.jet"));
            byte id = 0;
            jetPlayer.queueJetSegment(0,-1,0,0,0,id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        JetPlayer.getJetPlayer().play();
    }

    @Override
    protected void onPause() {
        super.onPause();

        JetPlayer.getJetPlayer().pause();
    }
}
