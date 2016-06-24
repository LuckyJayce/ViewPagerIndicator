package com.shizhefei.indicator;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by LuckyJayce on 2016/6/24.
 */
public class DisplayUtil {
    /**
     * 根据dip值转化成px值
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dipToPix(Context context, int dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return size;
    }
}
