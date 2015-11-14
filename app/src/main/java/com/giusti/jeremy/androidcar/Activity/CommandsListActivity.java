package com.giusti.jeremy.androidcar.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.R;

import java.util.ArrayList;

/**
 * Created by jgiusti on 22/10/2015.
 */
public class CommandsListActivity extends AppCompatActivity {

    public static final String COMMAND_LIST_STR = "command list as strng array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cmd);

        String[] cmdListStr;
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            cmdListStr = b.getStringArray(COMMAND_LIST_STR);
        } else {
            cmdListStr = manuallyLoadCmdList();
        }

        ListView commandListView = (ListView) this.findViewById(R.id.commands_lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.cmd_item, cmdListStr);
        commandListView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cmdlist_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_close:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String[] manuallyLoadCmdList() {
        ArrayList<String> cmdLst = new ArrayList<>();
        cmdLst.add("Trigger: "+ACPreference.getTrigger(this));
        cmdLst.add(getString(R.string.command_show_cmd));
        cmdLst.add(getString(R.string.command_show_settings));
        cmdLst.add(getString(R.string.command_listen));
        cmdLst.add(getString(R.string.command_stop_listening));
        cmdLst.add(getString(R.string.command_call));
        cmdLst.add(getString(R.string.command_speaker));
        cmdLst.add(getString(R.string.command_grid_display));
        cmdLst.add(getString(R.string.command_grid_hide));
        cmdLst.add(getString(R.string.command_touch));
        cmdLst.add(getString(R.string.command_swipe));
        cmdLst.add(getString(R.string.command_longclick));
        cmdLst.add(getString(R.string.command_write));
        cmdLst.add(getString(R.string.command_delete));
        cmdLst.add(getString(R.string.command_home));
        cmdLst.add(getString(R.string.command_back));
        cmdLst.add(getString(R.string.command_volume));
        cmdLst.add(getString(R.string.command_volume_up));
        cmdLst.add(getString(R.string.command_volume_down));
        cmdLst.add(getString(R.string.command_quit));
        return cmdLst.toArray(new String[cmdLst.size()]);
    }
}
