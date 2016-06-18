package com.giusti.jeremy.androidcar.Commands;

import android.text.TextUtils;

import com.giusti.jeremy.androidcar.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jgiusti on 16/10/2015.
 * execute command by using adb
 * <br> see also {@link ApiCmdExecutor} and {@link AppCmdExecutor}
 */
public class TerminalCmdExecutor {

    private final ICommandExcecutionResult resultListener;
    DataOutputStream shellInput;
    BufferedReader shellOutput;
    Process process = null;
    private String cmdPrefix = "";

    public TerminalCmdExecutor(ICommandExcecutionResult commandResultListener) {
        this.resultListener = commandResultListener;
    }

    public void connect() {
        cmdPrefix = "";
        String devices = executeCommande("adb devices", false, false).trim();
        String[] linesDevice = devices.split("\r\n|\r|\n");
        if (linesDevice.length > 1 && !TextUtils.isEmpty(linesDevice[1])) {
            for (int i = 1; i < linesDevice.length; i++) {
                if (linesDevice[i].contains("device")) {
                    cmdPrefix = "adb -s " + linesDevice[i].split("\t")[0] + " shell ";
                    break;
                }
            }
        }
        if (TextUtils.isEmpty(cmdPrefix)) {
            executeCommande("adb connect 127.0.0.1", false, false);
            cmdPrefix = "adb shell ";
        }
    }

    public String executeCommande(String commande, boolean imediateResult, boolean sendResult) {
        String constuctedCmd = cmdPrefix + commande;
        try {
            process = Runtime.getRuntime().exec(constuctedCmd + "\n");

            // shellInput = new DataOutputStream(process.getOutputStream());
            shellOutput = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            //writing output

            if ((!imediateResult) || shellOutput.ready()) {
                StringBuilder total = new StringBuilder();
                String line = "";
                while ((line = shellOutput.readLine()) != null) {
                    total.append(line + "\n");
                }

                //process.destroy();
                shellOutput.close();
                if (sendResult)
                    resultListener.onResult(ICommandExcecutionResult.EResult.SUCCESS, R.string.trmnl_cmd_key, null);
                return total.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
            if (sendResult)
                resultListener.onResult(ICommandExcecutionResult.EResult.FAIL, R.string.trmnl_cmd_key, "failed to use the terminal");
        }
        return "";
    }


    public void closeConnection() {
//        try {
//            shellInput.close();
//            shellOutput.close();
//            if (process != null) {
//                process.destroy();
//                process = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
