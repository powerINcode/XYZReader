package com.example.xyzreader.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import org.jetbrains.annotations.NotNull;

/**
 * Created by powerman23rus on 25.01.2018.
 * Enjoy ;)
 */

public class ViewUtil {
    public static void onLoad(@NotNull final View view, @NotNull final Runnable onComplete) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onComplete.run();
            }
        });
    }

    public static Interpolator getAccelerateInterpolator(Context context) {
        return getInterpolator(context, android.R.interpolator.accelerate_decelerate);
    }

    public static Interpolator getInterpolator(Context context, int interpolatorId) {
        return AnimationUtils.loadInterpolator(context, interpolatorId);
    }
}
