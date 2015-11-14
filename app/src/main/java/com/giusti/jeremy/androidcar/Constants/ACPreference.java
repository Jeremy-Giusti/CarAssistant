package com.giusti.jeremy.androidcar.Constants;

import android.content.Context;
import android.content.SharedPreferences;

import com.giusti.jeremy.androidcar.R;

import java.util.ArrayList;

/**
 * Created by jgiusti on 19/10/2015.
 */
public class ACPreference {

    private static final String preferenceKey ="com.giusti.jeremy.androidcar";

    private static final String TRIGGER_WORD ="triggerword";
    public static final int TRIGGER_WORD_ID = 0;

    private static final String STATUSBAR_HEIGHT ="statusbarheight";
    public static final int STATUSBAR_HEIGHT_ID = 1;
    private static final String SOFT_BUTTON_BAR_HEIGHT ="softbuttonbarheight";
    public static final int SOFT_BUTTON_BAR_HEIGHT_ID = 2;
    private static final String GRID_PADDING_LEFT ="gridpaddingleft";
    public static final int GRID_PADDING_LEFT_ID = 3;
    private static final String GRID_ITEM_HEIGHT ="griditemheight";
    public static final int GRID_ITEM_HEIGHT_ID =4;
    private static final String GRID_ITEM_WIDTH ="griditemwidth";
    public static final int GRID_ITEM_WIDTH_ID = 5;
    private static final String GRID_COLUMN_NB ="gridColumnNb";
    public static final int GRID_COLUMN_NB_ID = 6;
    private static final String SHOW_GRID ="showgrid";
    public static final int SHOW_GRID_ID = 7;
    private static final String AUTO_CLOSE ="autocloseonlaunche";
    public static final int AUTO_CLOSE_ID = 8;


    private static final ArrayList<ISettingChangeListener> listenerList =new ArrayList<>();



    public static void setTrigger(Context context,String trigger){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putString(TRIGGER_WORD, trigger).apply();
        notifyListeners(TRIGGER_WORD_ID);
    }

    public static String getTrigger(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getString(TRIGGER_WORD, context.getString(R.string.command_trigger));
    }


    public static int getStatusBarHeight(Context context){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getInt(STATUSBAR_HEIGHT, 0);
    }

    public static void setStatusBarHeight(Context context,int height){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putInt(STATUSBAR_HEIGHT,height).apply();
        notifyListeners(STATUSBAR_HEIGHT_ID);
    }

    public static int getSoftbuttonBarHeight(Context context){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getInt(SOFT_BUTTON_BAR_HEIGHT, 0);
    }

    public static void setSoftbuttonBarHeight(Context context,int height){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putInt(SOFT_BUTTON_BAR_HEIGHT,height).apply();
        notifyListeners(SOFT_BUTTON_BAR_HEIGHT_ID);
    }

    public static void setGridPaddingLeft(Context context,int padding){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putInt(GRID_PADDING_LEFT,padding).apply();
        notifyListeners(GRID_PADDING_LEFT_ID);
    }

    public static int getGridPaddingLeft(Context context){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getInt(GRID_PADDING_LEFT, 0);
    }


    public static void setGridItemHeight(Context context,int height){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putInt(GRID_ITEM_HEIGHT,height).apply();
        notifyListeners(GRID_ITEM_HEIGHT_ID);
    }

    public static void setGridItemWidth(Context context,int witdh){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putInt(GRID_ITEM_WIDTH,witdh).apply();
        notifyListeners(GRID_ITEM_WIDTH_ID);
    }

    public static int getGridItemHeight(Context context){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getInt(GRID_ITEM_HEIGHT, 0);
    }
    public static int getGridItemWidth(Context context){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getInt(GRID_ITEM_WIDTH, 0);
    }

    public static void setGridColumnNb(Context context,int nbColumn){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putInt(GRID_COLUMN_NB,nbColumn).apply();
        notifyListeners(GRID_COLUMN_NB_ID);
    }

    public static int getGridColumnNb(Context context){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getInt(GRID_COLUMN_NB, 0);
    }

    public static void setShowGrid(Context context,boolean show){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(SHOW_GRID, show).apply();
        notifyListeners(SHOW_GRID_ID);
    }

    public static boolean getShowGrid(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getBoolean(SHOW_GRID, true);
    }

    public static void setAutoClose(Context context,boolean show){
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(AUTO_CLOSE, show).apply();
        notifyListeners(AUTO_CLOSE_ID);
    }

    public static boolean getAutoClose(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);
        return prefs.getBoolean(AUTO_CLOSE, true);
    }


    //----------------------------------------- listener management -------------------------------
    private static void notifyListeners(int id){
        for(ISettingChangeListener listener :listenerList){
            listener.onSettingChanged(id);
        }
    }
    public static void addListener (ISettingChangeListener listener){
        if(!listenerList.contains(listener)){
            listenerList.add(listener);
        }
    }

    public static void removeListener(ISettingChangeListener listener){
        listenerList.remove(listener);
    }
}
