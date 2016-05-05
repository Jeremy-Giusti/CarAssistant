package com.giusti.jeremy.androidcar.SpeechRecognition;

import java.util.ArrayList;

/**
 * Created by jgiusti on 21/10/2015.
 */
public interface ISpeechResultListener {
    public void onSpeechResult(ArrayList<String> speechResult);

    public void onStartListening();

    public void onStopListening();
}
