package com.ahao.myapplication

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ahao.bannerview.BannerIndicator
import com.ahao.bannerview.BannerLayoutManager
import com.ahao.bannerview.ScaleBannerLayoutManager
import com.ahao.bannerview.BannerSetting
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_banner_view.*

class BannerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_view)
        initData1()
        initData2()
    }

    private fun initData1() {
        banner_view.layoutManager = ScaleBannerLayoutManager()
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
        banner_view.indicator = banner_indicator
        banner_view.setUp(BannerSetting().apply {
            slideTimeGap = 3000
            autoSlideSpeed = 1000
            loop = true
            canAutoSlide = true
        }, Adapter())
    }

    private val data = listOf("https://www.wanandroid.com/blogimgs/fa822a30-00fc-4e0d-a51a-d704af48205c.jpeg",
            "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png",
            "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png")

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.banner_image)
        val cardItem: CardView = itemView.findViewById(R.id.card_item)
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics
    }

    private fun initData2() {
        banner_view2.layoutManager = BannerLayoutManager()
        banner_view2.setUp(BannerSetting().apply {
            slideTimeGap = 3000 // 自动滑动的时间间隔
            autoSlideSpeed = 1000 // 自动滑动一次的速度
            loop = true // 是否循环滑动
            canAutoSlide = true // 是否自动滑动
        }, object : RecyclerView.Adapter<ImageViewHolder>() {
            override fun onCreateViewHolder(group: ViewGroup, viewType: Int): ImageViewHolder {
                val item = LayoutInflater.from(this@BannerViewActivity).inflate(R.layout.view_banner_item_view2, group, false)
                return ImageViewHolder(item)
            }

            override fun getItemCount() = data.size

            override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
                Glide.with(this@BannerViewActivity).load(data[position]).into(holder.image)
            }
        })

    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.banner_image)
    }
}