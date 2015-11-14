package com.giusti.jeremy.androidcar.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.ACService;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jgiusti on 22/10/2015.
 */
public class SettingActivity extends AppCompatActivity {

    private static final int TRIGGER_CHANGE_CODE = 7153;
    private ImageButton change_service_ib;
    private TextView trigger_tv;
    private Switch showGrid_switch;
    private Switch autoClose_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();

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
        //TODO
        change_service_ib = (ImageButton) findViewById(R.id.start_stop_service_ib);
        trigger_tv = (TextView) findViewById(R.id.trigger_tv);
        showGrid_switch = (Switch) findViewById(R.id.show_grid_switch);
        autoClose_switch = (Switch) findViewById(R.id.auto_close_switch);

        trigger_tv.setText(ACPreference.getTrigger(this));
        showGrid_switch.setChecked(ACPreference.getShowGrid(this));
        autoClose_switch.setChecked(ACPreference.getAutoClose(this));
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
            case TRIGGER_CHANGE_CODE: {
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
                    }
                }
                break;
            }

        }
    }


}
