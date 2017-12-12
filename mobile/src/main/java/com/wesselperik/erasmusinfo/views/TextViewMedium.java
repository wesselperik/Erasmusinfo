package com.wesselperik.erasmusinfo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by wesselperik on 12/12/2017.
 */

public class TextViewMedium extends android.support.v7.widget.AppCompatTextView {
    public TextViewMedium(Context context) {
        super(context);
        initText();
    }

    public TextViewMedium(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    public TextViewMedium(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initText();
    }

    private void initText(){
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/ProductSansRegular.ttf");
        this.setTypeface(customFont);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

}
