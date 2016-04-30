package com.giusti.jeremy.androidcar.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.Constants.Constants;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;
import com.giusti.jeremy.androidcar.Utils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jgiusti on 22/10/2015.
 */
public class SettingActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int TRIGGER_CHANGE_CODE = 7153;
    private ImageButton change_service_ib;
    private TextView trigger_tv;
    private Switch showGrid_switch;
    private Switch autoClose_switch;
    private Switch useTrigger_switch;
    private static final String[] permissions = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ACPreference.setStatusBarHeight(this, Utils.getStatusBarHeight(this));
        ACPreference.setSoftbuttonBarHeight(this, Utils.getSoftbuttonsbarHeight(this));
        initView();
        managePermissionGranting();

    }

    private void managePermissionGranting() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ACPreference.getPermissionGranted(this)) {
                askForStandartPermissions();
            }
        } else {
            ACPreference.setPermissionGranted(this, true);
            startACService();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_validate:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        change_service_ib = (ImageButton) findViewById(R.id.start_stop_service_ib);
        trigger_tv = (TextView) findViewById(R.id.trigger_tv);
        showGrid_switch = (Switch) findViewById(R.id.show_grid_switch);
        autoClose_switch = (Switch) findViewById(R.id.auto_close_switch);
        useTrigger_switch = (Switch) findViewById(R.id.use_trigger_switch);

        trigger_tv.setText(ACPreference.getTrigger(this));
        showGrid_switch.setChecked(ACPreference.getShowGrid(this));
        autoClose_switch.setChecked(ACPreference.getAutoClose(this));
        useTrigger_switch.setChecked(ACPreference.getUseTrigger(this));

        if (ACService.getInstance() != null) {
            change_service_ib.setImageResource(R.drawable.ic_action_stop);
        } else {
            change_service_ib.setImageResource(R.drawable.ic_action_start);
        }

        initEvents();
    }

    private void initEvents() {
        showGrid_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ACPreference.setShowGrid(SettingActivity.this, isChecked);
            }
        });
        autoClose_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ACPreference.setAutoClose(SettingActivity.this, isChecked);
            }
        });
        useTrigger_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ACPreference.setUseTrigger(SettingActivity.this, isChecked);
            }
        });


    }

    private void askForStandartPermissions() {
        ActivityCompat.requestPermissions(this, permissions, Constants.REQUEST_CALL_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CALL_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.all_permission_needed, Toast.LENGTH_LONG).show();
                    this.finish();
                    return;
                }
            }
            askForOverlayPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, Constants.REQUEST_OVERLAY_PERMISSION);
        }
    }

    public void onClickChangeTrigger(View v) {
        promptSpeechInput();
    }

    public void onClickShowCommands(View v) {
        Intent intent = new Intent(this, CommandsListActivity.class);
        startActivity(intent);
    }

    public void onClickChangeServiceState(View v) {
        if (ACService.getInstance() != null) {
            stopService(new Intent(this, ACService.class));
            change_service_ib.setImageResource(R.drawable.ic_action_start);
        } else {
            startService(new Intent(this, ACService.class));
            change_service_ib.setImageResource(R.drawable.ic_action_stop);
        }
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.trigger_change_condition));
        try {
            startActivityForResult(intent, TRIGGER_CHANGE_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech recognition not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TRIGGER_CHANGE_CODE:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String newTrigger = result.get(0).trim();
                    if (newTrigger.contains(" ")) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.trigger_change_condition),
                                Toast.LENGTH_SHORT).show();
                        promptSpeechInput();
                    } else {
                        trigger_tv.setText(newTrigger);
                        ACPreference.setTrigger(this, newTrigger);
                    }
                }
                break;

            case Constants.REQUEST_OVERLAY_PERMISSION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        if (ACService.getInstance() != null) {
                            ACService.getInstance().stopSelf();
                        }
                        startService(new Intent(this, ACService.class));
                        ACPreference.setPermissionGranted(this, true);
                    } else {
                        Toast.makeText(this, R.string.all_permission_needed, Toast.LENGTH_LONG).show();
                        this.finish();
                    }
                }
                break;
        }
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

}
