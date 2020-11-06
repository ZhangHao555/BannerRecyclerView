package com.ahao.myapplication.banner;

import android.view.View;

public class ScaleBannerLayoutManager extends BannerLayoutManager {
    private float heightScale = 0.9f;
    private float widthScale = 0.9f;

    protected void doWithItem() {
        if (heightScale >= 1 || widthScale >= 1) {
            return;
        }

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            float itemMiddle = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.0f;
            float screenMiddle = mOrientationHelper.getTotalSpace() / 2.0f;
            float interval = Math.abs(screenMiddle - itemMiddle) * 1.0f;
            if (interval - 0f < 0.0001) {
                continue;
            }
            float ratio = 1 - (1 - heightScale) * (interval / itemWidth);
            float ratioWidth = 1 - (1 - widthScale) * (interval / itemWidth);
            child.setScaleX(ratioWidth);
            child.setScaleY(ratio);
        }
    }
}
