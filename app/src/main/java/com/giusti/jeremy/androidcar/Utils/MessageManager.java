package com.giusti.jeremy.androidcar.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;
import com.giusti.jeremy.androidcar.Service.IExcecutionResult;

/**
 * Created by jérémy on 01/05/2016.
 */
public class MessageManager {

    private static final String SENT = "sent";
    private static final String SMS_RESULT = "sms result";
    private static MessageManager instance;

    private IExcecutionResult resultListener;

    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    private MessageManager() {
    }

    public IExcecutionResult getResultListener() {
        return resultListener;
    }

    public void setResultListener(IExcecutionResult resultListener) {
        this.resultListener = resultListener;
    }

    public void sendSms(String number, String message) {
        PendingIntent sendIntent = prepareForDefaultResultIntent();
        SmsManager.getDefault().sendTextMessage(number, null, message, sendIntent, null);
    }

    public void sendSms(String number, String message, PendingIntent sentIntent) {
        SmsManager.getDefault().sendTextMessage(number, null, message, sentIntent, null);

    }

    private PendingIntent prepareForDefaultResultIntent() {
        PendingIntent result = null;
        ACService acService = ACService.getInstance();
        if (acService != null) {
            Intent sentIntent = new Intent(SENT);
            /*Create Pending Intents*/
            result = PendingIntent.getBroadcast(
                    acService, 0, sentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            acService.registerBroadcast(SMS_RESULT, smsResultReceiver, new IntentFilter(SENT));
        }
        return result;
    }

    BroadcastReceiver smsResultReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int commandKey = R.string.send_sms_key;
            if (resultListener != null) {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        resultListener.onResult(IExcecutionResult.EResult.SUCCESS, commandKey, null);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        resultListener.onResult(IExcecutionResult.EResult.FAIL, commandKey, "generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        resultListener.onResult(IExcecutionResult.EResult.FAIL, commandKey, "radio off");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        resultListener.onResult(IExcecutionResult.EResult.FAIL, commandKey, "null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        resultListener.onResult(IExcecutionResult.EResult.FAIL, commandKey, "no service");
                        break;
                }

            }
        }

    };

}
