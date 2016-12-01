package com.a360ground.epubreader360.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.a360ground.epubreader360.R;
import com.a360ground.epubreader360.utils.UiUtil;


public class StyleableTextView extends TextView {

    public StyleableTextView(Context context) {
        super(context);
    }

    public StyleableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.StyleableTextView,
                R.styleable.StyleableTextView_font);
    }

    public StyleableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.StyleableTextView,
                R.styleable.StyleableTextView_font);
    }

}
