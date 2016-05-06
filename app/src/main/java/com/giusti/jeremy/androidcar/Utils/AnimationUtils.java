package com.giusti.jeremy.androidcar.Utils;

import android.view.animation.Animation;

/**
 * Created by jérémy on 06/05/2016.
 */
public class AnimationUtils {

    /**
     * call callback on animation end but remove existing AnimationListener on animation
     * @param callback
     * @param animation
     */
    public static void bindToAnimation(final IAnimationEndCallback callback, Animation animation) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                callback.onAnimationEnded();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public interface IAnimationEndCallback {
        void onAnimationEnded();
    }
}
