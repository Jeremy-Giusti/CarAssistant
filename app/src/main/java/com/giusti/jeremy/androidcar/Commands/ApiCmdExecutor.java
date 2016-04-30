package com.giusti.jeremy.androidcar.Commands;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Activity.CommandsListActivity;
import com.giusti.jeremy.androidcar.Activity.SettingActivity;
import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by jgiusti on 20/10/2015.
 * excecute command with the basic android api
 */
public class ApiCmdExecutor {

    private static final String TAG = ApiCmdExecutor.class.getSimpleName();
    private Context context;


    public ApiCmdExecutor(Context context) {
        this.context = context;
    }

    /**
     * end the service
     *
     * @param
     */
    public void finishApp() {
        if (ACService.getInstance() != null) {
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
    }

    public void setSpeaker(boolean on) {
        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        am.setSpeakerphoneOn(on);
    }

    public void openCmdListActivity(String[] strings) {
        Intent intent = new Intent(context, CommandsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle b = new Bundle();
        b.putStringArray(CommandsListActivity.COMMAND_LIST_STR, strings);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    public void openSettingActivity() {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void showGridOnOverlay(boolean show) {
        ACPreference.setShowGrid(context, show);
    }

    public boolean call(String number) {

        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, R.string.call_forbiden, Toast.LENGTH_SHORT).show();
                return false;
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
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
            Class serviceManagerStubClass;
            Class serviceManagerNativeClass;
            Class serviceManagerNativeStubClass;

            Method telephonyCall;
            Method telephonyEndCall;
            Method telephonyAnswerCall;
            Method getDefault;

            Method[] temps;
            Constructor[] serviceManagerConstructor;

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

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
            return false;
        }
        return true;
    }

}
