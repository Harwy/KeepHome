package com.shu.keephome.markview;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.shu.keephome.R;

import java.text.DecimalFormat;

/**
 * Created by 14623 on 2018/5/21.
 *
 */

public class MyMarkerView extends MarkerView {
    private TextView mContentTv;

    private IAxisValueFormatter xAxisValueFormatter;

    private DecimalFormat format;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        this.xAxisValueFormatter = xAxisValueFormatter;
        mContentTv = (TextView) findViewById(R.id.tv_content_marker_view);
        format = new DecimalFormat("###.0");
    }

    //回调函数每次MarkerView重绘,可以用来更新内容(用户界面)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        mContentTv.setText("" + e.getY());
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
