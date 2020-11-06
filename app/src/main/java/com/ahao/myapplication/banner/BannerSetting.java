package com.ahao.myapplication.banner;

public class BannerSetting {
    private boolean loop;               // 循坏
    private boolean canSlideByTouch;    // 是否允许手动滑动
    private boolean canAutoSlide;       // 是否允许自动滑动
    private int slideTimeGap;       // 自动滑动时间间隔
    private int autoSlideSpeed;         // 自动滑动一次的时长


    public boolean isLoop() {
        return loop;
    }

    public BannerSetting setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public boolean isCanSlideByTouch() {
        return canSlideByTouch;
    }

    public BannerSetting setCanSlideByTouch(boolean canSlideByTouch) {
        this.canSlideByTouch = canSlideByTouch;
        return this;
    }

    public boolean isCanAutoSlide() {
        return canAutoSlide;
    }

    public BannerSetting setCanAutoSlide(boolean canAutoSlide) {
        this.canAutoSlide = canAutoSlide;
        return this;
    }

    public int getSlideTimeGap() {
        return slideTimeGap;
    }

    public BannerSetting setSlideTimeGap(int slideTimeGap) {
        this.slideTimeGap = slideTimeGap;
        return this;
    }

    public int getAutoSlideSpeed() {
        return autoSlideSpeed;
    }

    public BannerSetting setAutoSlideSpeed(int autoSlideSpeed) {
        this.autoSlideSpeed = autoSlideSpeed;
        return this;
    }
}
