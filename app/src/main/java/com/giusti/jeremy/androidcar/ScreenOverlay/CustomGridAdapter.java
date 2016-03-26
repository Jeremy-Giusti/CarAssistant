package com.giusti.jeremy.androidcar.ScreenOverlay;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.giusti.jeremy.androidcar.Constants.ACPreference;
import com.giusti.jeremy.androidcar.Constants.Constants;
import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Utils;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jgiusti on 19/10/2015.
 */
public class CustomGridAdapter extends BaseAdapter {

    private Context mContext;
    private int itemWidth;
    private int itemHeight;
    private int maxNum;
    private int maxAlpha;
    private ArrayList<AlphaNumCoord> content;
    private HashMap<String, View> gridItem = new HashMap<>();
    private boolean showGridOnBorder = true;

    // Gets the context so it can be used later
    public CustomGridAdapter(Context c, int itemWidth, int itemHeight, ArrayList<AlphaNumCoord> content) {
        mContext = c;
        this.itemHeight = itemHeight;
        this.itemWidth = itemWidth;
        this.content = content;
        this.maxNum = content.get(content.size() - 1).getNum();
        this.maxAlpha = content.get(content.size() - 1).getAlpha();
        this.showGridOnBorder = ACPreference.getShowGrid(mContext);
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public Object getItem(int position) {
        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AlphaNumCoord data = content.get(position);
        TextView textView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(itemWidth, itemHeight));
            textView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(0, 0, 0, 0);
            textView.setTextSize((int)(itemHeight/(2.1 * Utils.convertDpToPixel(1,mContext))));
            textView.setSingleLine(true);
            convertView = textView;
            gridItem.put(data.toString(), convertView);
        }else{
            textView =(TextView)convertView;
        }

        if (data.getAlpha() == maxAlpha && showGridOnBorder) {
            textView.setText(Integer.toString(data.getNum()));
            textView.setBackgroundResource(R.drawable.right_grid);

        } else if (data.getNum() == maxNum && showGridOnBorder) {
            textView.setText(Character.toString(data.getAlpha()));
            textView.setBackgroundResource(R.drawable.bottom_grid);
        } else {
            textView.setText("");
            textView.setBackgroundResource(R.drawable.transparent);
        }

        return textView;
    }

    /**
     * display an effect on the clicked item
     *
     * @param alphaNumStr
     */
    public void eventOntItem(String alphaNumStr, int EventType) {
        final View view = gridItem.get(alphaNumStr);
        int animationDuration = 350;
        if (view != null) {
            final Drawable viewBg = view.getBackground();
            switch (EventType) {
                case Constants.EVENT_CLICK:
                    view.setBackgroundResource(android.R.color.holo_orange_dark);
                    break;
                case Constants.EVENT_LONGCLICK:
                    view.setBackgroundResource(android.R.color.holo_red_dark);
                    animationDuration = 1000;
                    break;
                case Constants.EVENT_SWIPE:
                    view.setBackgroundResource(android.R.color.holo_green_dark);
                    animationDuration = 425;
                    break;
                default:
                    view.setBackgroundResource(android.R.color.holo_red_dark);
                    break;

            }
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setDuration(animationDuration);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setBackground(viewBg);
                    view.setAlpha(1);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.setAnimation(fadeOut);
        }
    }

    public void showGridOnBorder(boolean show) {
        if (show != showGridOnBorder) {
            this.showGridOnBorder = show;
            notifyDataSetChanged();
        }
    }
}
