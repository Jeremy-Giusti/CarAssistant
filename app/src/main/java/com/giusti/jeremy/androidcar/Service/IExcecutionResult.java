package com.giusti.jeremy.androidcar.Service;

/**
 * Created by jérémy on 18/06/2016.
 */
public interface IExcecutionResult {
    public void onResult(EResult result, String commandKey, String details);

    public void onResult(EResult result, int commandKey, String details);

    public void onResult(EResult result, int commandKey, int details);


    enum EResult {
        SUCCESS,
        FAIL,
        MALFORMED
    }
}
