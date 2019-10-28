package com.Alan.eva.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 不会被挤压的list view
 *
 * @author 洋
 */
public class StretchableList extends ListView {

    public StretchableList(Context context) {
        super(context);
        setHorizontalFadingEdgeEnabled(false);
    }

    public StretchableList(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalFadingEdgeEnabled(false);
    }

    public StretchableList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHorizontalFadingEdgeEnabled(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
