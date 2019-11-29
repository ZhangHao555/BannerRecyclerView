package com.ahao.myapplication.banner;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;


import com.ahao.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BannerRecyclerActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view1)
    RecyclerView recyclerView1;
    BannerAdapter adapter1;

    @BindView(R.id.recycler_view2)
    RecyclerView recyclerView2;
    BannerAdapter adapter2;

    private List<String> data = new ArrayList<>();
    Random random = new Random();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_recycler_view);
        ButterKnife.bind(this);
        initData();
        initDataSource();
    }

    private void initData() {
        adapter1 = new BannerAdapter(data, this);
        adapter1.setItemWidth(1.0f);
        adapter1.setRatio(0.5f);

        BannerLayoutManager layoutManager1 = new BannerLayoutManager();
        layoutManager1.setHeightScale(1);
        layoutManager1.setWidthScale(1);
        recyclerView1.setAdapter(adapter1);
        recyclerView1.setLayoutManager(layoutManager1);

        BannerPageSnapHelper bannerPageSnapHelper1 = new BannerPageSnapHelper();
        bannerPageSnapHelper1.setInfinite(true);
        bannerPageSnapHelper1.attachToRecyclerView(recyclerView1);

        adapter2 = new BannerAdapter(data, this);
        BannerLayoutManager layoutManager2 = new BannerLayoutManager();
        adapter2.setItemWidth(0.8f);
        adapter2.setRatio(0.5f);
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(layoutManager2);

        BannerPageSnapHelper bannerPageSnapHelper2 = new BannerPageSnapHelper();
        bannerPageSnapHelper2.setInfinite(true);
        bannerPageSnapHelper2.attachToRecyclerView(recyclerView2);

    }

    private void initDataSource() {
        for (int i = 0; i < 10; i++) {
            data.add(i + "");
        }
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
    }

    @OnClick(R.id.scrollTo1)
    public void onClick1(View view) {
        int position = random.nextInt(data.size());
        Toast.makeText(this, "scroll_1 to position :" + position, Toast.LENGTH_SHORT).show();
        recyclerView1.smoothScrollToPosition(position);
    }

    @OnClick(R.id.scrollTo2)
    public void onClick2(View view) {
        int position = random.nextInt(data.size());
        Toast.makeText(this, "scroll_2 to position :" + position, Toast.LENGTH_SHORT).show();
        recyclerView2.smoothScrollToPosition(position);
    }

}
