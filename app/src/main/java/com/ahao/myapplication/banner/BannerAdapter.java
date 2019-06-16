package com.ahao.myapplication.banner;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ahao.myapplication.R;

import java.util.List;
import java.util.Random;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<String> data;

    private Context context;

    private Random random = new Random();

    private float itemWidth = 1.0f;
    private float ratio = 0.5f;  // 宽高比

    private DisplayMetrics displayMetrics ;
    public BannerAdapter(List<String> data, Context context) {
        super();
        this.data = data;
        this.context = context;
        displayMetrics = context.getResources().getDisplayMetrics();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_banner_item, parent,false);
        BannerViewHolder bannerViewHolder = new BannerViewHolder(view);
        return bannerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
        holder.textView.setBackgroundColor(Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
        ViewGroup.LayoutParams layoutParams = holder.textView.getLayoutParams();
        layoutParams.width = (int) (displayMetrics.widthPixels * itemWidth);
        layoutParams.height = (int) (layoutParams.width * ratio);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public BannerViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

    }

    public float getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(float itemWidth) {
        this.itemWidth = itemWidth;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
