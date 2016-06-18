package com.giusti.jeremy.androidcar.Commands;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;
import com.giusti.jeremy.androidcar.Utils.MessageManager;

import java.lang.reflect.Method;

/**
 * Created by jgiusti on 20/10/2015.
 * excecute command with the basic android api
 * <br> see also {@link TerminalCmdExecutor} and {@link AppCmdExecutor}
 */
public class ApiCmdExecutor {

    private static final String TAG = ApiCmdExecutor.class.getSimpleName();
    private Context context;
    private ICommandExcecutionResult resultListener;


    public ApiCmdExecutor(Context context, ICommandExcecutionResult resultListener) {
        this.context = context;
        this.resultListener = resultListener;
    }

    /**
     * end the service
     *
     * @param
     */
    public void finishApp() {
        if (ACService.getInstance() != null) {
            resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.end_assistant_key, null);
            context.stopService(new Intent(context, ACService.class));
        }
    }

    public void setVolume(int volume) {
        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                (int) ((am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) * (volume / 10.0)),
                0);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.set_volume_key, null);

    }

    public void setCallVolume(int volume) {
        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                (int) ((am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) * (volume / 10.0)),
                0);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.set_volume_key, null);
    }

    public void setSpeaker(boolean on) {
        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        am.setSpeakerphoneOn(on);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.set_speaker_key, null);

    }


    public void showGridOnOverlay(boolean show) {
        ACPreference.setShowGrid(context, show);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.grid_show_key, null);

    }

    public boolean call(String number) {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(context, R.string.call_forbiden, Toast.LENGTH_SHORT).show();
            return false;
        }
        context.startActivity(intent);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.call_key, null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setCallVolume(10);
                setSpeaker(true);
            }
        }, 1000);

        return true;
    }


    public boolean endCall() {
        try {
            //String serviceManagerName = "android.os.IServiceManager";
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class telephonyClass;
            Class telephonyStubClass;
            Class serviceManagerClass;
            Class serviceManagerNativeClass;

            Method telephonyEndCall;

            // Method getService;
            Object telephonyObject;
            Object serviceManagerObject;

            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);

            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

            telephonyObject = serviceMethod.invoke(null, retbinder);
            //telephonyCall = telephonyClass.getMethod("call", String.class);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            //telephonyAnswerCall = telephonyClass.getMethod("answerRingingCall");

            telephonyEndCall.invoke(telephonyObject);
            resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.end_call_key, null);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
            resultListener.onResult(ICommandExcecutionResult.EResult.MALFORMED, R.string.end_call_key, "could not connect to telephony subsystem");
            return false;
        }
        return true;
    }

    public boolean sendTo(String number, String message) {
        MessageManager.getInstance().setResultListener(resultListener);
        MessageManager.getInstance().sendSms(number, message);
        return true;
    }


    public void goHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
        resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.go_home_key, null);
    }
}
