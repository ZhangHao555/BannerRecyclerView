package com.ahao.myapplication.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ahao.bannerview.Indicator;
import com.ahao.myapplication.R;

import java.lang.ref.WeakReference;

public class BannerView extends FrameLayout {
    private BannerSetting setting;
    private BannerLayoutManager layoutManager;
    private BannerPageSnapHelper snapHelper;
    private RecyclerView.Adapter adapter = null;
    private Indicator indicator;
    private TimeHandler timeHandler;

    private RecyclerView recyclerView;

    private float actionDownX;
    private float actionDownY;

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_banner_view, this, true);
        recyclerView = view.findViewById(R.id.banner_recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (indicator != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        indicator.onViewSelected(layoutManager.getCurrentPosition());
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (indicator != null) {
                    indicator.onScrolled(dx, dx * 1.0f / layoutManager.itemWidth);
                }
            }
        });

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownX = ev.getX();
                actionDownY = ev.getY();
                requestDisallowInterceptTouchEvent(true);
                stopAutoSlide();
                break;

            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();
                if (Math.abs(actionDownX - moveX) > Math.abs(actionDownY - moveY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                if (!setting.isCanSlideByTouch()) {
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (setting.isCanAutoSlide()) {
                    startAutoSlide();
                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void startAutoSlide() {
        if (timeHandler != null) {
            timeHandler.sendEmptyMessageDelayed(TimeHandler.START_SCROLL, setting.getSlideTimeGap());
        }
    }

    private void stopAutoSlide() {
        if (timeHandler != null) {
            timeHandler.removeCallbacksAndMessages(null);
        }
    }

    private static class TimeHandler extends Handler {
        private WeakReference<BannerView> bannerView;

        private static final int START_SCROLL = 1;

        private TimeHandler(BannerView view) {
            this.bannerView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            BannerView view = bannerView.get();
            if (view == null) {
                return;
            }
            if (msg.what == START_SCROLL) {
                int curPos = view.getLayoutManager().getCurrentPosition();
                view.smoothScrollToPosition(++curPos);
                sendEmptyMessageDelayed(START_SCROLL, view.layoutManager.getSmoothScrollTime() + view.setting.getSlideTimeGap());
            }
        }
    }

    public void setUp(BannerSetting setting, RecyclerView.Adapter adapter) {
        this.setting = setting;
        this.adapter = adapter;

        layoutManager.setLoop(setting.isLoop());
        layoutManager.setSmoothScrollTime(setting.getAutoSlideSpeed());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerView);
        snapHelper.setLoop(setting.isLoop());

        if (setting.isCanAutoSlide()) {
            timeHandler = new TimeHandler(this);
            startAutoSlide();
        }

    }

    public BannerSetting getSetting() {
        return setting;
    }

    public void setSetting(BannerSetting setting) {
        this.setting = setting;
    }

    public BannerLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(BannerLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public BannerPageSnapHelper getSnapHelper() {
        return snapHelper;
    }

    public void setSnapHelper(BannerPageSnapHelper snapHelper) {
        this.snapHelper = snapHelper;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void smoothScrollToPosition(int position) {
        recyclerView.smoothScrollToPosition(position);
    }
}
