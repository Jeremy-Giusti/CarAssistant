package com.giusti.jeremy.androidcar.Commands;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.Constants.Constants;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.ScreenOverlay.AlphaNumCoord;
import com.giusti.jeremy.androidcar.ScreenOverlay.CoordinateConverter;
import com.giusti.jeremy.androidcar.Utils.ContactManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by jgiusti on 20/10/2015.
 * allow to interprete a single aor a group of string and can {@link ApiCmdExecutor} depending on the command
 */
public class CmdInterpretor {

    private static final String ALPHANUM_COORD_REGEX = "[a-z]\\d+";
    private static final String TAG = CmdInterpretor.class.getSimpleName();
    private final ICommandExcecutionResult commandResultListener;

    private Context context;

    private TerminalCmdExecutor trmCmdExec;
    private ApiCmdExecutor apiCmdExec;
    private AppCmdExecutor appCmdExec;

    private CoordinateConverter converter;
    private ContactManager mContactManager;

    private ArrayList<IMotionEventCmdListener> listenerList = new ArrayList<>();
    private boolean listening = true;

    //TODO command holder (with all commmand +get list ) ?
    private ArrayList<String> cmdListStr = new ArrayList<>();

    private String command_trigger;

    private String command_show_cmd;
    private String command_show_settings;

    private String command_listen;
    private String command_stop_listening;

    private String command_speaker;
    private String command_call;
    private String command_end_call;

    private String command_send;

    private String command_grid_display;
    private String command_grid_hide;

    private String command_touch;
    private String command_swipe;
    private String command_longclick;
    private String command_write;
    private String command_delete;
    private String command_home;
    private String command_back;
    private String command_volume;
    private String command_volume_up;
    private String command_music_play;
    private String command_music_pause;
    private String command_music_stop;
    private String command_music_next;
    private String command_music_previous;
    private String command_music_song;
    private String command_volume_down;
    private String command_quit;
    private boolean useTrigger = false;


    public CmdInterpretor(Context context, ICommandExcecutionResult commandResultListener, IMotionEventCmdListener... listeners) {
        this.context = context;
        this.commandResultListener = commandResultListener;
        if (listeners != null) {
            for (IMotionEventCmdListener listener : listeners) {
                this.listenerList.add(listener);
            }
        }

        trmCmdExec = new TerminalCmdExecutor(commandResultListener);
        apiCmdExec = new ApiCmdExecutor(context, commandResultListener);
        appCmdExec = new AppCmdExecutor(context, commandResultListener);

        converter = new CoordinateConverter(context);
        mContactManager = new ContactManager(context);

        trmCmdExec.connect();

        loadAllCommands();
    }

    //-------------------------------------------------------------SORTING ZONE --------------------------------------------------------------------------------//

    public boolean managePotentialCmdList(ArrayList<String> potentialCmdList) {
        for (String potentialCmd : potentialCmdList) {
            InterpretationResult cmdResult = interpreteCmd(potentialCmd);
            if (cmdResult.equals(InterpretationResult.APPLIED) || cmdResult.equals(InterpretationResult.EXECUTION_FAILED)) {
                Log.i(TAG, "Cmd executed: " + potentialCmd + " : " + cmdResult);
                return true;
            } else {
                Log.i(TAG, "Cmd not executed: " + potentialCmd + "\n result: " + cmdResult);
            }
        }
        commandResultListener.onResult(ICommandExcecutionResult.EResult.MALFORMED, R.string.cmd_malformed_key, R.string.no_cmd_found);
        return false;
    }


    public InterpretationResult interpreteCmd(String cmd) {

        InterpretationResult result = InterpretationResult.NOT_A_COMMAND;

        if (!useTrigger || StringUtils.containsIgnoreCase(cmd, command_trigger)) {
            result = InterpretationResult.UNINTERPRETED;

            if (!listening) {
                if (cmd.toLowerCase().matches(command_listen)) {
                    listening = true;
                    result = InterpretationResult.APPLIED;
                } else {
                    return InterpretationResult.NOT_LISTENING;
                }
            } else {
                if (cmd.toLowerCase().matches(command_stop_listening)) {
                    listening = false;
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_show_cmd)) {
                    appCmdExec.openCmdListActivity(cmdListStr.toArray(new String[cmdListStr.size()]));
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_show_settings)) {
                    appCmdExec.openSettingActivity();
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_end_call)) {
                    if (apiCmdExec.endCall()) {
                        result = InterpretationResult.APPLIED;
                    } else {
                        result = InterpretationResult.EXECUTION_FAILED;
                    }
                } else if (StringUtils.containsIgnoreCase(cmd, command_call)) {
                    result = interpreteCallCmd(cmd);
                } else if (StringUtils.containsIgnoreCase(cmd, command_send)) {
                    result = interpreteSendCmd(cmd);
                } else if (cmd.toLowerCase().matches(command_speaker)) {
                    apiCmdExec.setSpeaker(true);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_music_play)) {
                    appCmdExec.changeMusicState(AppCmdExecutor.MusicState.PLAY);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_music_pause)) {
                    appCmdExec.changeMusicState(AppCmdExecutor.MusicState.PAUSE);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_music_stop)) {
                    appCmdExec.changeMusicState(AppCmdExecutor.MusicState.STOP);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_music_next)) {
                    appCmdExec.changeMusicState(AppCmdExecutor.MusicState.NEXT);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_music_previous)) {
                    appCmdExec.changeMusicState(AppCmdExecutor.MusicState.PREVIOUS);
                    result = InterpretationResult.APPLIED;
                } else if (StringUtils.containsIgnoreCase(cmd, command_music_song)) {
                    result = interpretePlaySongCmd(cmd);
                } else if (cmd.toLowerCase().matches(command_grid_display)) {
                    apiCmdExec.showGridOnOverlay(true);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_grid_hide)) {
                    apiCmdExec.showGridOnOverlay(false);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_swipe)) {
                    result = interpetSwipeCmd(cmd);
                } else if (cmd.toLowerCase().matches(command_longclick)) {
                    result = interpetLongClickCmd(cmd);
                } else if (cmd.toLowerCase().matches(command_touch)) {
                    result = interpetTouchCmd(cmd);
                } else if (StringUtils.containsIgnoreCase(cmd, command_write)) {
                    result = interpretWriteCmd(cmd);
                } else if (StringUtils.containsIgnoreCase(cmd, command_delete)) {
                    result = interpretDelCmd(cmd);
                } else if (cmd.toLowerCase().matches(command_home)) {
                    //trmCmdExec.executeCommande("input keyevent 3", true);
                    apiCmdExec.goHome();
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_back)) {
                    trmCmdExec.executeCommande("input keyevent 4", true, true);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_volume)) {
                    result = interpretVolumeCmd(cmd);
                } else if (cmd.toLowerCase().matches(command_volume_up)) {
                    trmCmdExec.executeCommande("input keyevent 24", true, true);
                    trmCmdExec.executeCommande("input keyevent 24", true, true);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_volume_down)) {
                    trmCmdExec.executeCommande("input keyevent 25", true, true);
                    trmCmdExec.executeCommande("input keyevent 25", true, true);
                    result = InterpretationResult.APPLIED;
                } else if (cmd.toLowerCase().matches(command_quit)) {
                    apiCmdExec.finishApp();
                    result = InterpretationResult.APPLIED;
                }
            }
        }
        return result;
    }


    private InterpretationResult interpreteSendCmd(String cmd) {
        String[] splitCommand = cmd.split(command_send);

        boolean trigger = !ACPreference.getUseTrigger(context);
        String number = "";
        String message = "";
        for (String str : splitCommand) {
            if (StringUtils.containsIgnoreCase(str, command_trigger)) {
                trigger = true;
            } else if (trigger && !TextUtils.isEmpty(str)) {

                //find contact
                String contact = str.split(" ")[1];

                if (mContactManager.contactExist(contact)) {
                    number = mContactManager.getNumberFromName(contact);
                    message = str.replaceFirst(contact, "");
                } else {
                    //TODO try something different to find the contact
                    return InterpretationResult.UNINTERPRETED;
                }

            }
        }
        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(message)) {
            if (apiCmdExec.sendTo(number, message)) {
                return InterpretationResult.APPLIED;
            } else {
                return InterpretationResult.EXECUTION_FAILED;
            }
        } else {
            return InterpretationResult.UNINTERPRETED;
        }
    }


//-------------------------------------------------------------- Single command parse and execute zone ----------------------------------------------//

    /**
     * extract the alphanum/numeric coord if found and execute the touch event (notify the grid view too)
     *
     * @param touchCmdStr
     * @return
     */
    private InterpretationResult interpetTouchCmd(String touchCmdStr) {
        Point touchCoord = getCoordPoint(touchCmdStr);
        if (touchCoord != null && touchCoord.x >= 0 && touchCoord.y >= 0) {
            trmCmdExec.executeCommande("input tap " + touchCoord.x + " " + touchCoord.y, true, true);
            notifityAllListener(Constants.EVENT_CLICK, touchCoord);
            return InterpretationResult.APPLIED;
        } else {
            return InterpretationResult.UNINTERPRETED;
        }
    }

    /**
     * extract the alphanum/numeric coord if found and execute the LongClick event (notify the grid view too)
     *
     * @param longClickCmdStr
     * @return
     */
    private InterpretationResult interpetLongClickCmd(String longClickCmdStr) {
        Point touchCoord = getCoordPoint(longClickCmdStr);
        if (touchCoord != null && touchCoord.x >= 0 && touchCoord.y >= 0) {
            trmCmdExec.executeCommande("input touchscreen swipe " + touchCoord.x + " " + touchCoord.y + " " + touchCoord.x + " " + touchCoord.y + " 1500", true, true);
            notifityAllListener(Constants.EVENT_LONGCLICK, touchCoord);
            return InterpretationResult.APPLIED;
        } else {
            return InterpretationResult.UNINTERPRETED;
        }
    }

    private InterpretationResult interpetSwipeCmd(String swipeCmdStr) {
        Pair<Point, Point> touchCoordPair = getCoordPointPair(swipeCmdStr);
        Point touchCoord1 = touchCoordPair.first;
        Point touchCoord2 = touchCoordPair.second;

        if (touchCoord1 != null && touchCoord1.x >= 0 && touchCoord1.y >= 0 && touchCoord2 != null && touchCoord2.x >= 0 && touchCoord2.y >= 0) {
            trmCmdExec.executeCommande("shell input touchscreen swipe " + touchCoord1.x + " " + touchCoord1.y + " " + touchCoord2.x + " " + touchCoord2.y + " 500", true, true);
            notifityAllListener(Constants.EVENT_SWIPE, touchCoord1, touchCoord2);
            return InterpretationResult.APPLIED;
        } else {
            return InterpretationResult.UNINTERPRETED;
        }
    }

    private InterpretationResult interpretWriteCmd(String writeCmd) {
        String[] splitCommand = writeCmd.split(command_write);
        boolean trigger = !ACPreference.getUseTrigger(context);
        String textToWrite = "";
        for (String str : splitCommand) {
            if (StringUtils.containsIgnoreCase(str, command_trigger)) {
                trigger = true;
            } else if (trigger) {
                textToWrite += str;
            }
        }
        if (!TextUtils.isEmpty(textToWrite)) {
            textToWrite = textToWrite.replace(" ", "%s");
            trmCmdExec.executeCommande("shell input text " + textToWrite, true, true);
            return InterpretationResult.APPLIED;
        } else {
            return InterpretationResult.UNINTERPRETED;
        }

    }

    private InterpretationResult interpretDelCmd(String cmd) {
        String[] splitCommand = cmd.split(command_delete);
        boolean trigger = !ACPreference.getUseTrigger(context);
        int nbDel = -1;
        for (String str : splitCommand) {
            if (StringUtils.containsIgnoreCase(str, command_trigger)) {
                trigger = true;
            } else if (trigger && StringUtils.isNumeric(str.trim())) {
                nbDel = Integer.parseInt(str.trim());
            }
        }
        if (nbDel >= 0) {
            String cmdDel = "shell input keyevent 67";
            String cmdDelFinal = cmdDel;
            for (int i = 0; i < nbDel; i++) {
                cmdDelFinal += (" & " + cmdDel);
            }
            trmCmdExec.executeCommande(cmdDelFinal, true, true);
            return InterpretationResult.APPLIED;
        } else {
            return InterpretationResult.UNINTERPRETED;
        }
    }

    /**
     * 2 possibilities:
     * - the user want to call a number => parse number -> call
     * - the user want to call a contact => parse contact name => get number -> call
     *
     * @param callCmd
     * @return
     */
    private InterpretationResult interpreteCallCmd(String callCmd) {
        String[] splitCommand = callCmd.split(command_call);
        boolean trigger = !ACPreference.getUseTrigger(context);
        String number = "";
        for (String str : splitCommand) {
            if (StringUtils.containsIgnoreCase(str, command_trigger)) {
                trigger = true;
            } else if (trigger) {
                if (str.matches(".*\\d+.*")) {
                    //subject is a number
                    String[] PotentialSubjectList = str.split("\\s+");
                    for (String subjectPotential : PotentialSubjectList) {
                        if (StringUtils.isNumeric(subjectPotential)) {
                            number += subjectPotential;
                        } else if (!TextUtils.isEmpty(number)) {
                            break;
                        }
                    }
                } else {
                    //subject is a name
                    String[] PotentialSubjectList = str.split("\\s+");
                    for (String subjectPotential : PotentialSubjectList) {
                        if (mContactManager.contactExist(subjectPotential)) {
                            number = mContactManager.getNumberFromName(subjectPotential);
                            break;
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(number)) {
            if (apiCmdExec.call(number)) {
                return InterpretationResult.APPLIED;
            } else {
                return InterpretationResult.EXECUTION_FAILED;
            }
        } else {
            return InterpretationResult.UNINTERPRETED;
        }
    }

    private InterpretationResult interpretePlaySongCmd(String cmd) {
        String[] splitCommand = cmd.split(command_music_song);
        boolean trigger = !ACPreference.getUseTrigger(context);
        for (String str : splitCommand) {
            if (StringUtils.containsIgnoreCase(str, command_trigger)) {
                trigger = true;
            } else if (trigger) {

                if (!TextUtils.isEmpty(str)) {
                    appCmdExec.playMusic(str.trim());
                    return InterpretationResult.APPLIED;
                }

            }
        }
        return InterpretationResult.UNINTERPRETED;

    }

    private InterpretationResult interpretVolumeCmd(String cmd) {
        String[] splitCommand = cmd.split(command_volume);
        boolean trigger = !ACPreference.getUseTrigger(context);
        int volumeLvl = -1;
        for (String str : splitCommand) {
            if (StringUtils.containsIgnoreCase(str, command_trigger)) {
                trigger = true;
            } else if (trigger && StringUtils.isNumeric(str.trim())) {
                volumeLvl = Integer.parseInt(str.trim());
            }
        }
        if (volumeLvl >= 0) {
            apiCmdExec.setVolume(volumeLvl);
            return InterpretationResult.APPLIED;
        } else {
            return InterpretationResult.UNINTERPRETED;
        }
    }

    /**
     * return a single coord found in the cmd
     *
     * @param cmdStr
     * @return
     */
    @Nullable
    private Point getCoordPoint(String cmdStr) {
        String[] splitCommand = cmdStr.split("\\s+");
        boolean trigger = !ACPreference.getUseTrigger(context);
        Point touchCoord = null;
        for (String word : splitCommand) {
            if (StringUtils.containsIgnoreCase(word, command_trigger)) {
                trigger = true;
            } else if (trigger && (word.matches(ALPHANUM_COORD_REGEX))) {
                //ALPHANUM
                AlphaNumCoord touchAlphnumCoord = new AlphaNumCoord(Integer.parseInt(word.substring(1)), word.charAt(0));
                touchCoord = converter.getXYFromAlphaNum(touchAlphnumCoord);
                break;

            } else if (trigger && StringUtils.isNumeric(word)) {
                //numerical
                if (touchCoord == null || touchCoord.x < 0) {
                    touchCoord = new Point(Integer.parseInt(word), -1);
                } else {
                    touchCoord.y = Integer.parseInt(word);
                    break;
                }
            }
        }
        return touchCoord;
    }

    /**
     * return a pair of coord found on the comd string
     *
     * @param cmdStr
     * @return
     */
    @Nullable
    private Pair<Point, Point> getCoordPointPair(String cmdStr) {
        String[] splitCommand = cmdStr.split("\\s+");
        boolean trigger = !ACPreference.getUseTrigger(context);
        Point touchCoord1 = null;
        Point touchCoord2 = null;

        for (String word : splitCommand) {
            if (StringUtils.containsIgnoreCase(word, command_trigger)) {
                trigger = true;
            } else if (trigger && (word.matches(ALPHANUM_COORD_REGEX))) {
                //ALPHANUM
                AlphaNumCoord touchAlphnumCoord = new AlphaNumCoord(Integer.parseInt(word.substring(1)), word.charAt(0));
                if (touchCoord1 == null) {
                    touchCoord1 = converter.getXYFromAlphaNum(touchAlphnumCoord);
                } else {
                    touchCoord2 = converter.getXYFromAlphaNum(touchAlphnumCoord);
                    break;
                }
            } else if (trigger && StringUtils.isNumeric(word)) {
                //numerical
                if (touchCoord1 == null || touchCoord1.y < 0) {
                    if (touchCoord1 == null) {
                        touchCoord1 = new Point(Integer.parseInt(word), -1);
                    } else {
                        touchCoord1.y = Integer.parseInt(word);
                    }
                } else {
                    if (touchCoord2 == null) {
                        touchCoord2 = new Point(Integer.parseInt(word), -1);
                    } else {
                        touchCoord2.y = Integer.parseInt(word);
                        break;
                    }
                }
            }
        }
        return new Pair<>(touchCoord1, touchCoord2);
    }

    //------------------------------------------------------ listener zone -------------------------------------------//

    private void notifityAllListener(int eventType, Point... coordinates) {
        for (IMotionEventCmdListener listener : this.listenerList) {
            listener.onMotionEventCmd(eventType, coordinates);
        }
    }

    public void addListener(IMotionEventCmdListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeListener(IMotionEventCmdListener listener) {
        listenerList.remove(listener);
    }

    public void clearListeners() {
        listenerList.clear();
    }

    //------------------------------------------------------  loading zone ---------------------------------------------//

    /**
     * load all command string from ressource
     */
    private void loadAllCommands() {

        useTrigger = ACPreference.getUseTrigger(context);

        command_trigger = ACPreference.getTrigger(context);
        cmdListStr.add("Trigger: " + command_trigger);

        command_show_cmd = context.getString(R.string.command_show_cmd);
        cmdListStr.add(command_show_cmd);
        command_show_settings = context.getString(R.string.command_show_settings);
        cmdListStr.add(command_show_settings);

        command_listen = context.getString(R.string.command_listen);
        cmdListStr.add(command_listen);
        command_stop_listening = context.getString(R.string.command_stop_listening);
        cmdListStr.add(command_stop_listening);

        command_call = context.getString(R.string.command_call);
        cmdListStr.add(command_call);
        command_end_call = context.getString(R.string.command_end_call);
        cmdListStr.add(command_end_call);
        command_speaker = context.getString(R.string.command_speaker);
        cmdListStr.add(command_speaker);

        command_send = context.getString(R.string.command_send_to);
        cmdListStr.add(command_send);

        command_grid_display = context.getString(R.string.command_grid_display);
        cmdListStr.add(command_grid_display);
        command_grid_hide = context.getString(R.string.command_grid_hide);
        cmdListStr.add(command_grid_hide);

        command_touch = context.getString(R.string.command_touch);
        cmdListStr.add(command_touch);
        command_swipe = context.getString(R.string.command_swipe);
        cmdListStr.add(command_swipe);
        command_longclick = context.getString(R.string.command_longclick);
        cmdListStr.add(command_longclick);
        command_write = context.getString(R.string.command_write);
        cmdListStr.add(command_write);
        command_delete = context.getString(R.string.command_delete);
        cmdListStr.add(command_delete);
        command_home = context.getString(R.string.command_home);
        cmdListStr.add(command_home);
        command_back = context.getString(R.string.command_back);
        cmdListStr.add(command_back);
        command_volume = context.getString(R.string.command_volume);
        cmdListStr.add(command_volume);
        command_volume_up = context.getString(R.string.command_volume_up);
        cmdListStr.add(command_volume_up);
        command_volume_down = context.getString(R.string.command_volume_down);
        cmdListStr.add(command_volume_down);

        command_music_play = context.getString(R.string.command_play_music);
        cmdListStr.add(command_music_play);
        command_music_pause = context.getString(R.string.command_pause_music);
        cmdListStr.add(command_music_pause);
        command_music_stop = context.getString(R.string.command_stop_music);
        cmdListStr.add(command_music_stop);
        command_music_next = context.getString(R.string.command_next_music);
        cmdListStr.add(command_music_next);
        command_music_previous = context.getString(R.string.command_previous_music);
        cmdListStr.add(command_music_previous);

        command_music_song = context.getString(R.string.command_play_song);
        cmdListStr.add(command_music_song);

        command_quit = context.getString(R.string.command_quit);
        cmdListStr.add(command_quit);
    }

    public void triggerChanged() {
        cmdListStr.remove(0);
        useTrigger = ACPreference.getUseTrigger(context);
        command_trigger = ACPreference.getTrigger(context);
        cmdListStr.add(0, "Trigger: " + command_trigger);
    }

    public void endAllConnections() {
        this.trmCmdExec.closeConnection();
    }

    public enum InterpretationResult {
        APPLIED,
        UNINTERPRETED,
        NOT_A_COMMAND,
        NOT_LISTENING,
        EXECUTION_FAILED;
    }
}
