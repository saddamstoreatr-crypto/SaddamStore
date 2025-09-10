package com.sdstore.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class CustomTextView extends View {
    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
