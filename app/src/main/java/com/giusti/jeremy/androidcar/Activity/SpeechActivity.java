package com.giusti.jeremy.androidcar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.giusti.jeremy.androidcar.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by jgiusti on 16/10/2015.
 */
public class SpeechActivity extends AppCompatActivity implements RecognitionListener {

    private static final String TAG = SpeechActivity.class.getSimpleName();
    private SpeechRecognizer mySpeechRecognizer;
    private TextView speechTv;
    private StringBuilder speechHolder = new StringBuilder();
    private boolean isListening = false;
    private ImageButton listenButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mySpeechRecognizer.setRecognitionListener(this);
        speechTv = (TextView) this.findViewById(R.id.speechTv);
        listenButton = (ImageButton) this.findViewById(R.id.listen_button);

    }

    public void startListenClick(View v) {
        if (!isListening) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //      intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            mySpeechRecognizer.startListening(intent);
            isListening = true;
            Log.d(TAG, "starting voice recognition");
            listenButton.setImageResource(R.drawable.ic_action_stoptlisten);
        } else {
            mySpeechRecognizer.stopListening();
            isListening = false;
            Log.d(TAG, "ending voice recognition");
            listenButton.setImageResource(R.drawable.ic_action_speech);

        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        try {
            String str = new String(buffer, "UTF-8");
            speechTv.setText(str);
            Log.d(TAG, str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEndOfSpeech() {
        speechTv.setText("");
        speechHolder = new StringBuilder();
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults " + results);
        ArrayList<String> potentialCmdList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < potentialCmdList.size(); i++) {
            Log.d(TAG, "result " + potentialCmdList.get(i));
            speechHolder.append(potentialCmdList.get(i) + "\n");
        }
        speechTv.setText(speechHolder.toString());
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
