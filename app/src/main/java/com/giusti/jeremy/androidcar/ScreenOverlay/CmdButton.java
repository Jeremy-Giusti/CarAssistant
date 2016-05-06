package com.giusti.jeremy.androidcar.ScreenOverlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Service.IFloatingButtonClickListener;
import com.giusti.jeremy.androidcar.Utils.Utils;

/**
 * Created by jérémy on 28/04/2016.
 * display a simple movable action button
 * TODO extract touch listener to just have event like longClick/click/move
 */
public class CmdButton extends RelativeLayout {
    // if the event if 30 px away from it starting position it is a movement and not a click anymore
    private final static int differenceBetweenClickAndMove = 20;

    private static final long LONG_CLICK_TIME = 750;
    public static final int ANIMATION_DURATION_MILLIS = 400;

    private final IFloatingButtonClickListener clicksListener;
    private final Context context;
    private final ImageButton cmdButton;
    private final RelativeLayout layout;
    private boolean endServiceMode = false;
    private WindowManager.LayoutParams rootParams;
    private WindowManager mWm;
    private boolean buttonShrinked = false;

    private Pair<Integer, Integer> screenDimen;
    private int statusBarHeight;
    private int defaultButtonSize;

    public CmdButton(Context context, IFloatingButtonClickListener clicksListener) {
        super(context);
        this.clicksListener = clicksListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cmd_button, this, true);

        cmdButton = (ImageButton) findViewById(R.id.cmd_button);
        layout = (RelativeLayout) findViewById(R.id.cmd_button_layout);
        this.context = context;

        screenDimen = Utils.getScreenSize(context);
        statusBarHeight = ACPreference.getStatusBarHeight(context);
        defaultButtonSize = Utils.getIntPixelFromDimens(R.dimen.cmd_button_size, context);
        initButtonEvents(context);
    }

    public void showInWindow(WindowManager wm) {
        mWm = wm;
        rootParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        rootParams.gravity = Gravity.CENTER | Gravity.CENTER;
        rootParams.setTitle("Load Average");
        wm.addView(this, rootParams);
    }

    private void initButtonEvents(final Context context) {
        cmdButton.setOnTouchListener(cmdButtonEventListener);
    }

    private void onClickButton() {
        if (endServiceMode) {
            clicksListener.onSecondaryClick();
        } else {
            clicksListener.onprimaryClick();
        }
    }

    private void moveButton(float deltaX, float deltaY) {
        if (mWm == null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cmdButton.getLayoutParams();
            params.topMargin += (int) deltaY;
            params.leftMargin += (int) deltaX;
            cmdButton.setLayoutParams(params);
        } else {

            rootParams.y += (int) deltaY;
            rootParams.x += (int) deltaX;

            mWm.updateViewLayout(this, rootParams);
        }
    }


    private void onEndMovingButton(float evenx, float eventy) {

        if (evenx < cmdButton.getWidth() / 2) {
            shrinkButton(Gravity.LEFT, R.anim.shrink_to_top_left);
        } else if (eventy < (cmdButton.getHeight() / 2 + statusBarHeight)) {
            shrinkButton(Gravity.TOP, R.anim.shrink_to_top_left);
        } else if ((screenDimen.first - evenx) < cmdButton.getWidth() / 2) {
            shrinkButton(Gravity.RIGHT, R.anim.shrink_to_right);
        } else if ((screenDimen.second - eventy) < cmdButton.getHeight() / 2) {
            shrinkButton(Gravity.BOTTOM, R.anim.shrink_to_bottom);
        }
    }

    private void shrinkButton(final int gravity, int anim) {
        if (!buttonShrinked) {

            buttonShrinked = true;

            final Animation shrinkAnim = AnimationUtils.loadAnimation(context, anim);

            ViewGroup.LayoutParams params = cmdButton.getLayoutParams();
            params.width = params.width / 2;
            params.height = params.height / 2;
            cmdButton.setLayoutParams(params);

            cmdButton.startAnimation(shrinkAnim);
            layout.setGravity(gravity);

        }
    }

    private void unShrinkButton() {
        if (buttonShrinked) {

            final ScaleAnimation growAnim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f);

            growAnim.setDuration(ANIMATION_DURATION_MILLIS);

            ViewGroup.LayoutParams params = cmdButton.getLayoutParams();
            params.width = defaultButtonSize;
            params.height = defaultButtonSize;

            cmdButton.setLayoutParams(params);
            cmdButton.startAnimation(growAnim);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.setGravity(Gravity.CENTER);
                    buttonShrinked = false;
                }
            }, ANIMATION_DURATION_MILLIS);

        }
    }

    /**
     * change button mode to end service or normal
     */
    private void longClickButton() {
        if (endServiceMode) {
            cmdButton.setImageDrawable(context.getDrawable(R.drawable.ic_action_speech));
            cmdButton.setBackground(context.getDrawable(R.drawable.fab_shape));
        } else {
            cmdButton.setImageDrawable(context.getDrawable(R.drawable.ic_action_close));
            cmdButton.setBackground(context.getDrawable(R.drawable.fab_shape_red));
        }
        cmdButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int padding = Utils.getIntPixelFromDimens(R.dimen.padding_cmd_button,context);
        cmdButton.setPadding(padding,padding,padding,padding);
        endServiceMode = !endServiceMode;
    }

    private OnTouchListener cmdButtonEventListener = new View.OnTouchListener() {
        private float startX = -1;
        private float startY = -1;
        private float previousX = -1;
        private float previousY = -1;

        private boolean isMoving = false;

        private boolean longClicked = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float eventX = event.getRawX();
            float eventY = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = eventX;
                    startY = eventY;
                    previousX = eventX;
                    previousY = eventY;
                    unShrinkButton();
                    launchLongClickTimer();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isMoving) {
                        float diffX = (eventX < startX) ? startX - eventX : eventX - startX;
                        float diffY = (eventY < startY) ? startY - eventY : eventY - startY;
                        if (diffX > differenceBetweenClickAndMove || diffY > differenceBetweenClickAndMove) {
                            isMoving = true;
                            moveButton(eventX - startX, eventY - startY);
                        }
                    } else {
                        float moveX = eventXOnBorder(eventX) ? 0 : eventX - previousX;
                        float moveY = eventYOnBorder(eventY) ? 0 : eventY - previousY;
                        moveButton(moveX, moveY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isMoving) {
                        isMoving = false;
                        onEndMovingButton(eventX, eventY);
                        longClickHandler.removeCallbacks(longClickRunnable);
                    } else if (longClicked) {
                        longClicked = false;
                    } else {
                        onClickButton();
                        longClickHandler.removeCallbacks(longClickRunnable);
                    }
                    break;

            }
            previousX = eventX;
            previousY = eventY;
            return false;
        }

        private Handler longClickHandler = new Handler();
        private Runnable longClickRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isMoving) {
                    longClicked = true;
                    Utils.vibrate(context, 50, 1);
                    longClickButton();
                }
            }
        };

        private void launchLongClickTimer() {
            longClickHandler.postDelayed(longClickRunnable, LONG_CLICK_TIME);
        }
    };


    private boolean eventXOnBorder(float x) {
        int tolerence = (int) (defaultButtonSize / 2);
        if (x < 0 + tolerence) {
            return true;
        } else if (x > screenDimen.first - tolerence) {
            return true;
        }
        return false;
    }

    private boolean eventYOnBorder(float y) {
        int tolerence = (int) (defaultButtonSize / 2);
        if (y < statusBarHeight + tolerence) {
            return true;
        } else if (y > (screenDimen.second - tolerence)) {
            return true;
        }
        return false;
    }


}
