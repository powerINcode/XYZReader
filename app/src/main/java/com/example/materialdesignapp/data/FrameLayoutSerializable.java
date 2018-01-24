package com.example.materialdesignapp.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.io.Serializable;

/**
 * Created by powerman23rus on 24.01.2018.
 * Enjoy ;)
 */

public class FrameLayoutSerializable extends FrameLayout implements Serializable {
    public FrameLayoutSerializable(@NonNull Context context) {
        super(context);
    }

    public FrameLayoutSerializable(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayoutSerializable(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static FrameLayoutSerializable from(Context context, FrameLayout.LayoutParams flp) {
        FrameLayoutSerializable fls = new FrameLayoutSerializable(context);
        fls.setLayoutParams(flp);

        return fls;
    }
}
