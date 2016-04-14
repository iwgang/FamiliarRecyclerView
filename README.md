[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FamiliarRecyclerView-green.svg?style=true)](https://android-arsenal.com/details/1/2829)
[![@iwgang](https://img.shields.io/badge/weibo-%40iwgang-blue.svg)](http://weibo.com/iwgang)

# FamiliarRecyclerView
这是一个如你熟悉ListView、GridView一样熟悉的RecyclerView类库，你可以用以前使用ListView / GridView的习惯来使用RecyclerView，这些可让你将项目原有的ListView / GridView / 瀑布流 迁移到RecyclerView时减少许多工作量.

### 效果图
![](https://raw.githubusercontent.com/iwgang/FamiliarRecyclerView/master/screenshot/screenshot.gif)  

### gradle
    compile 'com.github.iwgang:familiarrecyclerview:1.3.0'

### 这些是不是很熟悉？
```
// 添加/删除 头部View (支持多个)
mRecyclerView.addHeaderView() 和 .removeHeaderView()

// 添加/删除 底部View (支持多个)
mRecyclerView.addFooterView() 和 .removeFooterView()

// 设置分割线（也可以在布局文件中直接指定分割线Divider及分割线大小，当然你也可以使用自己的分割线实现）
mRecyclerView.setDivider() 如果是网格或瀑布流视图，你甚至可以设置横竖不同的分割线Divider及分割线大小

// 设置数据空View（设置isRetainShowHeadOrFoot为true时，可以让显示EmptyView时不会清除掉添加的HeadView和FooterView）
mRecyclerView.setEmptyView()

// Item单击事件
mRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
    @Override
    public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
        // ...
    }
});

// Item长按事件
mRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
        return true;
    }
});

// 设置滚动到顶部或底部时的事件回调
mRecyclerView.setOnScrollListener(new FamiliarRecyclerViewOnScrollListener(mRecyclerView.getLayoutManager()) {
    @Override
    public void onScrolledToTop() {
        // top
    }

    @Override
    public void onScrolledToBottom() {
        // bottom
    }
});
等...
当然，RecyclerView原有的那些全部都能正常使用的
```

### 布局
``` 
// LinearLayout （ListView）
<cn.iwgang.familiarrecyclerview.FamiliarRecyclerView
    android:id="@+id/mRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:scrollbars="vertical"
    app:frv_divider="#696969"
    app:frv_dividerHeight="1dp"
    app:frv_emptyView="@id/tv_empty"
    app:frv_layoutManager="linear"
    app:frv_layoutManagerOrientation="vertical" />
    
// GridLayout （GridView）
<cn.iwgang.familiarrecyclerview.FamiliarRecyclerView
    android:id="@+id/mRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:scrollbars="vertical"
    app:frv_dividerHorizontal="#FFEE00"
    app:frv_dividerVertical="#FFCCDD"
    app:frv_dividerHorizontalHeight="10dp"
    app:frv_dividerVerticalHeight="30dp"
    app:frv_itemViewBothSidesMargin="20dp"
    app:frv_layoutManager="grid"
    app:frv_layoutManagerOrientation="vertical"
    app:frv_spanCount="3" />
    
// StaggeredGridLayout （瀑布流）
<cn.iwgang.familiarrecyclerview.FamiliarRecyclerView
      android:id="@+id/mRecyclerView"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:scrollbars="vertical"
      app:frv_divider="#EFADEF"
      app:frv_dividerHorizontalHeight="10dp"
      app:frv_dividerVerticalHeight="10dp"
      app:frv_itemViewBothSidesMargin="20dp"
      app:frv_layoutManager="staggeredGrid"
      app:frv_layoutManagerOrientation="vertical"
      app:frv_spanCount="2" />
```

### 下拉刷新 + 加载更多
在1.3.0版本开始，新增加了FamiliarRefreshRecyclerView来实现下拉刷新及加载更多
```
// 布局 （FamiliarRecyclerView的属性全可以使用，List、Gird、staggeredGrid均可设置）
<cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView
    android:id="@+id/cv_refreshListRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:scrollbars="vertical"
    app:frv_divider="#333333"
    app:frv_dividerHeight="0.5dp"
    app:frv_emptyView="@id/tv_empty"
    app:frv_isEmptyViewKeepShowHeadOrFooter="true"
    app:frv_layoutManager="linear"
    app:frv_layoutManagerOrientation="vertical" />

// 下拉刷新回调
mCvRefreshListRecyclerView.setOnPullRefreshListener(...)

// 加载更多回调
mCvRefreshListRecyclerView.setOnLoadMoreListener(...)

// 设置加载更多的View
mCvRefreshListRecyclerView.setLoadMoreView(...)

// 设置启动/停用下拉刷新
mCvRefreshListRecyclerView.setLoadMoreEnabled(true / false);

// 设置启动/停用加载更多
mCvRefreshListRecyclerView.setPullRefreshEnabled(true / false);
```

### 自定义配置
    参数 | 类型 | 默认值 | 说明
--- | --- | ---| ---
frv_divider                 | reference / color               | 无        | 全局分割线divider
frv_dividerVertical         | reference / color               | 无        | 垂直分割线divider
frv_dividerHorizontal       | reference / color               | 无        | 水平分割线divider
frv_dividerHeight           | dimension                      | 1px       | 全局分割线size
frv_dividerVerticalHeight   | dimension                      | 1px       | 垂直分割线size
frv_dividerHorizontalHeight | dimension                      | 1px       | 水平分割线size
frv_isNotShowGridEndDivider | boolean                        | false     | 是否不显示Grid最后item的分割线
frv_itemViewBothSidesMargin | dimension                      | 无        | itemView两边的边距（不会设置headerView和footerView的两边）
frv_emptyView               | reference                      | 无        | emptyView id
frv_isEmptyViewKeepShowHeadOrFooter | boolean                | false     | 显示EmptyView时，是否保留显示已设置的HeadView和FooterView
frv_layoutManager           | linear / grid / staggeredGrid  | 无        | 布局类型
frv_layoutManagerOrientation| horizontal / vertical          | vertical  | 布局方向
frv_spanCount               | integer                        | 2         | 格子数量，frv_layoutManager=grid / staggeredGrid时有效
frv_headerDividersEnabled   | boolean                        | false     | 是否启用headView中的分割线
frv_footerDividersEnabled   | boolean                        | false     | 是否启用footerView中的分割线


