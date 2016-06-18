package com.giusti.jeremy.androidcar.Commands;

/**
 * Created by jérémy on 18/06/2016.
 */
public class EmptyCommandResultListenerCommand implements ICommandExcecutionResult {
    @Override
    public void onResult(EResult result, String commandKey, String details) {
    }

    @Override
    public void onResult(EResult result, int commandKey, String details) {

    }

    @Override
    public void onResult(EResult result, int commandKey, int details) {

    }
}
