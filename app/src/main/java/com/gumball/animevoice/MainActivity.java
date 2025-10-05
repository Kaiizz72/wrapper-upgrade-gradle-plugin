package com.gumball.animevoice;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import be.tarsos.dsp.*;
import be.tarsos.dsp.effects.HighPass;
import be.tarsos.dsp.effects.Reverb;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;

public class MainActivity extends AppCompatActivity {
    private AudioDispatcher dispatcher;
    private Thread audioThread;
    private Button btnStart, btnStop;
    private Spinner voiceSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceSelector = findViewById(R.id.voiceSelector);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(v -> {
            startVoice(voiceSelector.getSelectedItemPosition());
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        });

        btnStop.setOnClickListener(v -> {
            stopVoice();
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        });
    }

    private void startVoice(int mode) {
        int sampleRate = 44100;
        int bufferSize = 1024;
        int overlap = 0;

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap);

        if (mode == 0) { // Anime Nữ
            float pitchRatio = (float) Math.pow(2, 12.0 / 12.0);
            dispatcher.addAudioProcessor(new PitchShifter(pitchRatio, bufferSize, overlap, sampleRate));
            dispatcher.addAudioProcessor(new HighPass(200, sampleRate));
            dispatcher.addAudioProcessor(new Reverb(0.2f));
        } else { // Anime Nam
            float pitchRatio = (float) Math.pow(2, -4.0 / 12.0);
            dispatcher.addAudioProcessor(new PitchShifter(pitchRatio, bufferSize, overlap, sampleRate));
            dispatcher.addAudioProcessor(new HighPass(80, sampleRate));
            dispatcher.addAudioProcessor(new Reverb(0.15f));
        }

        dispatcher.addAudioProcessor(new AndroidAudioPlayer(dispatcher.getFormat(), bufferSize));

        audioThread = new Thread(dispatcher, "AnimeVoiceThread");
        audioThread.start();
    }

    private void stopVoice() {
        if (dispatcher != null) {
            dispatcher.stop();
            dispatcher = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVoice();
    }
}
