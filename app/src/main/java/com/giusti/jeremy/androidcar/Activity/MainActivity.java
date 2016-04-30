package com.giusti.jeremy.androidcar.Activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Commands.CmdInterpretor;
import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;
import com.giusti.jeremy.androidcar.SpeechRecognition.ISpeechResultListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView screenSize;
    EditText cmdInput;
    CmdInterpretor cmdInterpretor;
    //TerminalCmdExecutor executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenSize = (TextView) this.findViewById(R.id.screenSizeTv);
        cmdInput = (EditText) this.findViewById(R.id.Cmd_et);
        ACPreference.setStatusBarHeight(this, this.getStatusBarHeight());
        ACPreference.setSoftbuttonBarHeight(this, this.getSoftbuttonsbarHeight());
        startACService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_speech:
                openSpeechActivity();
                return true;
            case R.id.action_setting:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void openSpeechActivity() {
        Intent intent = new Intent(this, SpeechActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        displayScreenResolution();
        super.onResume();
    }

    private void startACService() {
        if (ACService.getInstance() == null) {
            startService(new Intent(this, ACService.class));
            if (ACPreference.getAutoClose(this)) {
                Toast.makeText(this, R.string.assistant_started, Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }

    }


    private void displayScreenResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenSize.setText(size.x + " x " + size.y);
    }

    public void onClickSendCmd(View v) {
        if (ACService.getInstance() == null) {
            Toast.makeText(this, "Android car service not started yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String cmd = cmdInput.getText().toString();
        ArrayList<String> cmdlist = new ArrayList<String>();
        cmdlist.add(cmd);
        ACService.getInstance().onInputCmd(cmdlist);

    }

    public void onClickTopRight(View v) {
        ArrayList<String> cmdShowListCmd = new ArrayList<String>();
        cmdShowListCmd.add(getString(R.string.command_trigger) + " " + getString(R.string.command_show_cmd));
        ((ISpeechResultListener) ACService.getInstance()).onSpeechResult(cmdShowListCmd);
        //Toast.makeText(this, "touched on top right", Toast.LENGTH_SHORT).show();
    }

    public void onClickTopLeft(View v) {
        // Toast.makeText(this, "touched on top left", Toast.LENGTH_SHORT).show();
        ACService.getInstance().changelisteningMode();
    }

    public void onClickBottomRight(View v) {
        Toast.makeText(this, "touched on bottom right", Toast.LENGTH_SHORT).show();
    }

    public void onClickBottomLeft(View v) {
        Toast.makeText(this, "touched on bottom left", Toast.LENGTH_SHORT).show();
        if (ACService.getInstance() != null) {
            stopService(new Intent(this, ACService.class));

        } else {
            startService(new Intent(this, ACService.class));
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getSoftbuttonsbarHeight() {
        // getRealMetrics is only available with API 17 and +
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;

    }
}
