package com.giusti.jeremy.androidcar.Commands;

/**
 * Created by jérémy on 18/06/2016.
 */
public interface ICommandExcecutionResult {
    public void onResult(EResult result, String commandKey, String details);

    public void onResult(EResult result, int commandKey, String details);

    public void onResult(EResult result, int commandKey, int details);


    enum EResult {
        SUCCESS,
        FAIL,
        MALFORMED
    }
}
