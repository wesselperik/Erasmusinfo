package com.wesselperik.erasmusinfo;

/**
 * Created by Wessel on 7-3-2016.
 */
import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
public class WearableListItemLayout extends LinearLayout
        implements WearableListView.OnCenterProximityListener {
    private float mScale;
    private TextView mName;
    public WearableListItemLayout(Context context) {
        this(context, null);
    }
    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mName = (TextView) findViewById(R.id.title);
    }
    @Override
    public void onCenterPosition(boolean animate) {
        mName.setAlpha(1f);
    }
    @Override
    public void onNonCenterPosition(boolean animate) {
        mName.setAlpha(1f);
    }
}