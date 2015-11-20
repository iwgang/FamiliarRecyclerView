# FamiliarRecyclerView
这是一个如你熟悉ListView、GridView一样熟悉的RecyclerView的类库，你可以用以前使用ListView / GridView的习惯来使用RecyleerView，这些都可以让你项目中原有的ListView/GridView/瀑布流迁移到RecyclerView时减少很多工作量.

### 效果图
![](https://raw.githubusercontent.com/iwgang/FamiliarRecyclerView/master/screenshot/screenshot.gif)  

### 你可以
```
// 添加/删除头部View (支持多个)
mRecylcerView.addHeaderView() 和 .removeHeaderView()
// 添加/删除底部View (支持多个)
mRecylcerView.addFooterView() 和 .removeFooterView()
// 设置分割线（也可以在布局文件中直接指定分割线Divider及分割线大小，当然你也可以使用自己的分割线实现）
mRecylcerView.setsetDivider() 如果风格视图或瀑布流视图 你甚至可以设置横竖不同的分割线及分割线大小
// 设置数据空View（设置isRetainShowHeadOrFoot为true时，可以让显示EmptyView时不会清除掉添加的HeadView和FooterView）
mRecylcerView.setEmptyView()
// 设置滚到到顶部或底部时的事件回调
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

### 自定义配置
    参数 | 类型 | 默认值 | 说明
--- | --- | ---| ---
frv_divider                 | reference和color               | 无        | 全局分割线divider
frv_dividerVertical         | reference和color               | 无        | 垂直分割线divider
frv_dividerHorizontal       | reference和color               | 无        | 水平分割线divider
frv_dividerHeight           | dimension                      | 1px       | 全局分割线size
frv_dividerVerticalHeight   | dimension                      | 1px       | 垂直分割线size
frv_dividerHorizontalHeight | dimension                      | 1px       | 水平分割线size
frv_itemViewBothSidesMargin | dimension                      | 无        | itemView两边的边距（不会设置headerView和footerview的两边）
frv_emptyView               | reference                      | 无        | emptyView id
frv_layoutManager           | linear / grid / staggeredGrid  | 无        | 布局类型
frv_layoutManagerOrientation| horizontal / vertical          | vertical  | 布局方向
frv_spanCount               | integer                        | 2         | 格子数量，frv_layoutManager=grid / staggeredGrid时有效
frv_headerDividersEnabled   | boolean                        | false     | 是否启用headView中的分割线
frv_footerDividersEnabled   | boolean                        | false     | 是否启用footerView中的分割线

### 已知待修复Bug
1. 有设置HeadView或FooterView，并且setEmptyView时isRetainShowHeadOrFoot为true，在清除完data数据显示emptyView后，再添加数据可能会报异常
1. 动画删除格子布局的item时，如果dividerHeight设的比较大，View会有出现大小变大后再动画移除的情况
