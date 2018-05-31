package com.shu.keephome.markview;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by 14623 on 2018/5/31.
 *
 */

public class MyXFormatter implements IAxisValueFormatter {
    private String[] mValues;

    public MyXFormatter(String[] values) {
        this.mValues = values;
    }
    private static final String TAG = "MyXFormatter";

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value % mValues.length];
    }

}
