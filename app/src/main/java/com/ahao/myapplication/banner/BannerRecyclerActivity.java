package com.ahao.myapplication.banner;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.ahao.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BannerRecyclerActivity extends AppCompatActivity {

    BannerView bannerView;
    BannerAdapter adapter;

    private List<String> data = new ArrayList<>();
    Random random = new Random();

    private List<String> data1 = Arrays.asList("https://www.wanandroid.com/blogimgs/fa822a30-00fc-4e0d-a51a-d704af48205c.jpeg",
            "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png",
            "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png",
            "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png",
            "https://www.wanandroid.com/blogimgs/fa822a30-00fc-4e0d-a51a-d704af48205c.jpeg");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_recycler_view);
        ButterKnife.bind(this);
        initData();
        initDataSource();
    }

    private void initData() {
        adapter = new BannerAdapter(data, this);
        bannerView = findViewById(R.id.banner_view);
        BannerSetting setting = new BannerSetting().setAutoSlideSpeed(0)
                .setCanAutoSlide(false)
                .setCanSlideByTouch(true)
                .setLoop(true)
                .setSlideTimeGap(2000);
        bannerView.setLayoutManager(new ScaleBannerLayoutManager());
        bannerView.setSnapHelper(new BannerPageSnapHelper());
        bannerView.setUp(setting, adapter);

    }

    private void initDataSource() {
        data.clear();
        data.addAll(data1);
        adapter.notifyDataSetChanged();
    }


    @OnClick({R.id.scroll_to_position})
    public void onClick1(View view) {
        int position = random.nextInt(data.size());
        Toast.makeText(this, "scroll_1 to position :" + position, Toast.LENGTH_SHORT).show();
        bannerView.smoothScrollToPosition(position);
    }

}
