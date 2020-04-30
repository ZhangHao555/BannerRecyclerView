## 先看效果

![](https://user-gold-cdn.xitu.io/2019/12/1/16ec0dee39244ba5?w=371&h=579&f=gif&s=1790786)

## 实现功能
```
class BannerSetting(var loop: Boolean = false, // 是否循坏
        var canSlideByTouch: Boolean = true, // 是否允许手动滑动
        var canAutoSlide: Boolean = true, // 是否允许自动滑动
        var slideTimeGap: Long = 2000,  // 自动滑动时间间隔
        var autoSlideSpeed: Int = 500 // 自动滑动一次的时长

)
```

2. 滑动伴有缩放效果

## 使用方式

添加依赖
```
  implementation 'com.github.ZhangHao555:BannerRecyclerView:1.1.4'
```

使用方式类似于RecyclerView 设置一个layoutManager 和一个Adapter即可使用

```
//添加布局
    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.ahao.bannerview.BannerView
            android:id="@+id/banner_view"
            android:layout_width="match_parent"
            android:layout_height="160dp"
            android:layout_marginTop="10dp"
            app:layout_constraintTop_toTopOf="parent" />

        <com.ahao.bannerview.BannerIndicator
            android:id="@+id/banner_indicator"
            android:layout_width="25dp"
            android:layout_height="10dp"
            android:layout_marginBottom="5dp"
            app:layout_constraintBottom_toBottomOf="@id/banner_view"
            app:layout_constraintEnd_toEndOf="@id/banner_view"
            app:layout_constraintStart_toStartOf="@id/banner_view" />
    </androidx.constraintlayout.widget.ConstraintLayout>
    
```

```
  // 设置 LayoutManager， ScaleBannerLayoutManager是在滑动的时候有一个缩放的效果
  banner_view.layoutManager = ScaleBannerLayoutManager()
        // 指示器 可以不设置
        banner_indicator.adapter = object : BannerIndicator.Adapter() {

            override fun getItemCount(): Int = data.size

            override fun getUnselectedView(context: Context) = CircleView(context, null).apply {
                radius = dp2px(context, 2.5f)
                color = Color.parseColor("#E6E6E6")
            }

            override fun getSelectedView(context: Context) = CircleView(context, null).apply {
                radius = dp2px(context, 2.5f)
                color = Color.parseColor("#FF00CEAA")
            }
        }
         // 指示器 可以不设置
        banner_view.indicator = banner_indicator
        // 设置滑动参数
        banner_view.setUp(BannerSetting().apply {
            slideTimeGap = 3000  // 自动滑动时间间隔
            autoSlideSpeed = 1000 // 完成滑动一次的时间
            loop = true         // 是否可以循环滑动
            canAutoSlide = true // 是否允许手动滑动
        }, Adapter())

    // Adapter即为普通的RecyclerView的适配器
    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(group: ViewGroup, position: Int): ViewHolder {
            val item = LayoutInflater.from(this@BannerViewActivity).inflate(R.layout.view_banner_item_view, group, false)
            return ViewHolder(item)
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cardItem.layoutParams?.width = (getDisplayMetrics(this@BannerViewActivity).widthPixels.times(0.85f)).toInt()
            Glide.with(this@BannerViewActivity).load(data[position]).into(holder.image)
        }
    }
```


github地址 ：https://github.com/ZhangHao555/BannerRecyclerView
