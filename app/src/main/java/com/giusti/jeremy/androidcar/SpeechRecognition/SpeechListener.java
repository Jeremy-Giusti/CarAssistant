package com.giusti.jeremy.androidcar.SpeechRecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jgiusti on 21/10/2015.
 */
public class SpeechListener implements RecognitionListener {

    private static final String TAG = SpeechListener.class.getSimpleName();
    private SpeechRecognizer mSpeechRecognizer;
    private boolean shouldBeListening = false;
    private boolean isRestarting =false;
    private ArrayList<ISpeechResultListener> mListenerList = new ArrayList<>();
    private Context mContext;


    public SpeechListener(Context context, ISpeechResultListener... listeners) {
        this.mContext = context;

        for (ISpeechResultListener listener : listeners) {
            mListenerList.add(listener);
        }

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(this);

    }

    public void setListeningSpeech(boolean listening) {
        if (listening && !SpeechRecognizer.isRecognitionAvailable(mContext)) {
            Toast.makeText(mContext, "Speech Recognizer unavailable on device", Toast.LENGTH_SHORT).show();
            return;
        }
        if (listening) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //      intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            mSpeechRecognizer.startListening(intent);
            isRestarting =true;
            shouldBeListening = true;
            Log.d(TAG, "starting voice recognition");
        } else {
            mSpeechRecognizer.cancel();
            shouldBeListening = false;
            isRestarting=false;
            Log.d(TAG, "ending voice recognition");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        isRestarting =false;
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
       // restartListen();
    }

    @Override
    public void onError(int error) {
//        switch (error)
//        {
//            case SpeechRecognizer.ERROR_AUDIO://3
//               // message = "Audio recording error";
//                break;
//            case SpeechRecognizer.ERROR_CLIENT://5
//                //message = "Client side error";
//                break;
//            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS://9
//               // message = "Insufficient permissions";
//                //restart = false;
//                break;
//            case SpeechRecognizer.ERROR_NETWORK://2
//                //message = "Network error";
//                break;
//            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT://1
//                //message = "Network timeout";
//                break;
//            case SpeechRecognizer.ERROR_NO_MATCH://7
//                //message = "No match";
//                break;
//            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY://8
//                //message = "RecognitionService busy";
//                break;
//            case SpeechRecognizer.ERROR_SERVER://4
//                //message = "error from server";
//                break;
//            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT://6
//                //message = "No speech input";
//                break;
//            default:
//                //message = "Not recognised";
//                break;
//                    }

        Toast.makeText(mContext,"error : " + error,Toast.LENGTH_SHORT).show();
        if(error != 8 && error != 9){
            restartListen();
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults " + results);
        restartListen();
        ArrayList<String> potentialCmdList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        notifityAllListener(potentialCmdList);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Toast.makeText(mContext,"partial result",Toast.LENGTH_SHORT).show();
        //restartListen();
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public void restartListen(){
        if(shouldBeListening && !isRestarting) {
            this.mSpeechRecognizer.cancel();

            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
            mSpeechRecognizer.setRecognitionListener(this);
            //this.mSpeechRecognizer.stopListening();
            setListeningSpeech(true);
        }
    }

    //------------------------------------ listener management zone -------------------------------//
    private void notifityAllListener(ArrayList<String> result) {
        for (ISpeechResultListener listener : this.mListenerList) {
            listener.onSpeechResult(result);
        }
        //TODO provisoire
        Toast.makeText(mContext,"result :" + result.toString(),Toast.LENGTH_LONG).show();
    }

    public void addListener(ISpeechResultListener listener) {
        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    public void removeListener(ISpeechResultListener listener) {
        mListenerList.remove(listener);
    }

    public void clearListeners() {
        mListenerList.clear();
    }

    public boolean isShouldBeListening() {
        return shouldBeListening;
    }
}
