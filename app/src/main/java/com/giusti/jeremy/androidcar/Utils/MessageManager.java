package com.giusti.jeremy.androidcar.Utils;

import android.app.PendingIntent;
import android.telephony.SmsManager;

/**
 * Created by jérémy on 01/05/2016.
 */
public class MessageManager {

    private static MessageManager instance;
    //TODO faire un systeme de callBack pour savoir si un sms est partis

    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    public void sendSms(String number, String message/*, callback*/){
        SmsManager.getDefault().sendTextMessage(number,null,message,null,null);

    }


    public interface SmsResultListener{
        public void onSmsResult(SmsResult result);
    }

    public enum SmsResult{
        SEND,
        FAILED
    }

}
