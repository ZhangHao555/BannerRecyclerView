# BannerRecyclerView
Android Repository to implement Banner View

## 预览
很多app的首页都有一个可以滑动的banner。大概长这样：  

![](https://user-gold-cdn.xitu.io/2019/6/16/16b5f159d1079fcf?w=372&h=650&f=gif&s=627007)

实现方式有多种，介绍一种用RecyclerView的实现方式

## 实现思路
第一种平铺的banner 其实很好实现，就是一个RecycerView + PagerSnapHelper。但是为了兼容多种显示效果，例如第二种的显示效果，我们需要去自定义LayoutMmanager和SnapHelper。

### LayoutManager部分
自定义LayoutManager一般是分两步，**布局**和**滑动**，再想想LayoutManager需要些什么属性。

#### 属性
需要一个boolean值标识是否循环布局。
需要两个float值标识滑动时的宽高缩放。  
```
public class BannerLayoutManager{
    private float heightScale = 0.9f;
    private float widthScale = 0.9f;
    private boolean infinite = true;  //默认无限循环
    
    ...
    
}
```

#### 布局
1、计算第一个View的开始位置 : int offsetX = （父布局宽度 - 子View宽度） / 2  
```
 int offsetX = (mOrientationHelper.getTotalSpace() - mOrientationHelper.getDecoratedMeasurement(scrap)) / 2;
```

2、计算是否要添加一个view为第1个子view，以显示出循环布局的效果。

![](https://user-gold-cdn.xitu.io/2019/6/16/16b5f27e77acc70b?w=765&h=561&f=png&s=14127)
```
 View lastChild = getChildAt(getChildCount() - 1);
   // 如果是循环布局，并且最后一个view已超出父布局，则添加最左边的view
  if ( infinite && lastChild != null && getDecoratedRight(lastChild) > mOrientationHelper.getTotalSpace()) {
    layoutLeftItem(recycler);
  }
```

3、缩放所有的view  
缩放规则：以父布局的中线（中心线）为基准，如果子view的中线与中心线重合，则缩放比为1.0f；如果不重合，则计算出子view的中线与中心线的距离,距离越大，缩放比越小。 
```
 private void scaleItem() {
        if (heightScale >= 1 || widthScale >= 1) {
            return;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float itemMiddle = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.0f;
            float screenMiddle = mOrientationHelper.getTotalSpace() / 2.0f;
            float interval = Math.abs(screenMiddle - itemMiddle) * 1.0f;

            float ratio = 1 - (1 - heightScale) * (interval / itemWidth);
            float ratioWidth = 1 - (1 - widthScale) * (interval / itemWidth);
            child.setScaleX(ratioWidth);
            child.setScaleY(ratio);
        }
    }
```

4、总体的布局方法   
```
private void layoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 || state.isPreLayout()) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);

        View scrap = recycler.getViewForPosition(0);
        measureChildWithMargins(scrap, 0, 0);
        itemWidth = getDecoratedMeasuredWidth(scrap);
        int offsetX = (mOrientationHelper.getTotalSpace() - mOrientationHelper.getDecoratedMeasurement(scrap)) / 2;
        for (int i = 0; i < getItemCount(); i++) {
            if (offsetX > mOrientationHelper.getTotalSpace()) {
                break;
            }
            View viewForPosition = recycler.getViewForPosition(i);
            addView(viewForPosition);
            measureChildWithMargins(viewForPosition, 0, 0);
            offsetX += layoutItem(viewForPosition, offsetX);
        }

        View lastChild = getChildAt(getChildCount() - 1);
        // 如果是循环布局，并且最后一个view已超出父布局，则添加最左边的view
        if ( infinite && lastChild != null && getDecoratedRight(lastChild) > mOrientationHelper.getTotalSpace()) {
            layoutLeftItem(recycler);
        }
        scaleItem();
    }
```

#### 滑动
对滑动的处理就是为了对view的回收，以减少消耗，提高效率。 处理方式就是根据滑动距离去添加和删除view。  

我也是第一次自定义LayoutManager,感觉写得有点繁琐了。分了左滑右滑两种情况去写。
```
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return offsetDx(dx, recycler);
    }

    private int offsetDx(int dx, RecyclerView.Recycler recycler) {
        int realScroll = dx;
        // 向左
        if (dx > 0) {
            realScroll = scrollToLeft(dx, recycler, realScroll);
        }
        // 向右
        if (dx < 0) {
            realScroll = scrollToRight(dx, recycler, realScroll);
        }
        scaleItem();

        return realScroll;
    }
```
scrollToLeft或者scrollToRight都是只做了三件事，添加view，计算实际滑动距离并滑动，回收view

```
    private int scrollToLeft(int dx, RecyclerView.Recycler recycler, int realScroll) {
        while (true) {
            // 将需要添加的view添加到RecyclerView中
            View rightView = getChildAt(getChildCount() - 1);
            int decoratedRight = getDecoratedRight(rightView);
            if (decoratedRight - dx < mOrientationHelper.getTotalSpace()) {
                int position = getPosition(rightView);
                if (!infinite && position == getItemCount() - 1) {
                    break;
                }

                int addPosition = infinite ? (position + 1) % getItemCount() : position + 1;
                View lastViewAdd = recycler.getViewForPosition(addPosition);
                addView(lastViewAdd);
                measureChildWithMargins(lastViewAdd, 0, 0);
                int left = decoratedRight;
                layoutDecoratedWithMargins(lastViewAdd, left, getItemTop(lastViewAdd), left + getDecoratedMeasuredWidth(lastViewAdd), getItemTop(lastViewAdd) + getDecoratedMeasuredHeight(lastViewAdd));
            } else {
                break;
            }
        }

        // 处理滑动
        View lastChild = getChildAt(getChildCount() - 1);
        int left = getDecoratedLeft(lastChild);
        if (getPosition(lastChild) == getItemCount() - 1) {
            // 最后一个view已经到底了，计算实际可以滑动的距离
            if (left - dx < 0) {
                realScroll = left;
            }
        }
        offsetChildrenHorizontal(-realScroll);

        // 回收滑出父布局的view
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int decoratedRight = getDecoratedRight(child);
            if (decoratedRight < 0) {
                removeAndRecycleView(child, recycler);
            }
        }
        return realScroll;
    }
```

这样，自定义的LayoutManager基本就完成了。

### SnapHelper部分
自定义完成了LayoutManager的确可以高效的实现gif中的效果，但是滑动的时候就有问题了，RecyclerView默认是支持fling操作的，就是惯性滑动。而无法做到一次只滑动一页，并且居中显示的效果（类似ViewPager的滑动效果）。  
为了实现这种效果，google提供了一个SnapHelper抽象类，我们可以继承这个去实现自己的滑动逻辑。SDK提供了PagerSnapHelper和LinearSnapHelper两种实现。  
PagerSnapHelper可以做到ViewPager那种一次滑动一页的效果，但是当滑动到最后一个view的时候会明显的出现卡顿。因为PagerSnapHelper默认不支持循环布局这种情况的。所以我继承PagerSnaperHelper,修改了一点点逻辑，实现了循环滑动的效果。

```
public class BannerPageSnapHelper extends PagerSnapHelper {

    private boolean infinite = false;
    private OrientationHelper horizontalHelper;

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
                                      int velocityY) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        View mStartMostChildView = findStartView(layoutManager, getHorizontalHelper(layoutManager));

        if (mStartMostChildView == null) {
            return RecyclerView.NO_POSITION;
        }
        final int centerPosition = layoutManager.getPosition(mStartMostChildView);
        if (centerPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        final boolean forwardDirection;
        if (layoutManager.canScrollHorizontally()) {
            forwardDirection = velocityX > 0;
        } else {
            forwardDirection = velocityY > 0;
        }

        if (forwardDirection) {
            if (centerPosition == layoutManager.getItemCount() - 1) {
                return infinite ? 0 : layoutManager.getItemCount() - 1;
            } else {
                return centerPosition + 1;
            }
        } else {
            return centerPosition;
        }
    }

    private View findStartView(RecyclerView.LayoutManager layoutManager,
                               OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        int start = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int childStart = helper.getDecoratedStart(child);

            /** if child is more to start than previous closest, set it as closest  **/
            if (childStart < start) {
                start = childStart;
                closestChild = child;
            }
        }
        return closestChild;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (horizontalHelper == null) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return horizontalHelper;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }
}
```


### 扩展
关于banner部分，一般项目会有以下几个参数。  

1、style 展示样式：例如圆角 或是平铺。 可以在每个子view外面套一个CardView 去设置圆角，然后根据需求在adapter中设置view的宽高。  

2、是否循环显示：BannerLayoutManager和PagerHelper都有一个属性，infinite，为true时，循环显示。  

3、自动播放：这个在Activity或者Fragment中用Rxjava或者Handler加一个定时器，调用 recyclerView.smoothScrollToPosition(position)就行了 。 

4、滑动动画的显示时间：BannerLayoutManager中有个smoothScrollTime属性，调用set方法设置一下就行了。

应该能满足大多数需求吧。。

### 源码
想了解详情去看代码吧 https://github.com/ZhangHao555/BannerRecyclerView



