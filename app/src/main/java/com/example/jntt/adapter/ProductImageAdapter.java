package com.example.jntt.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import java.util.List;

/**
 * 项目职责：商品图片 Adapter，负责商品详情页顶部图片轮播展示。
 * 技术说明：刷新列表。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder> {

    private List<Object> imageList; // 可以是 Integer (资源 ID) 或 String (Uri)

    /**
     * 项目职责：创建商品图片轮播 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public ProductImageAdapter(List<Object> imageList) {
        this.imageList = imageList;
    }

    /**
     * 项目职责：商品图片 Adapter，负责商品详情页顶部图片轮播展示。
     * 关键调用：刷新列表。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setImageList(List<Object> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    /**
     * 项目职责：为商品图片轮播 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return new ImageViewHolder(imageView);
    }

    /**
     * 项目职责：把当前位置的数据绑定到商品图片轮播 Adapter的 item 布局控件上。
     * 关键调用：显示内置图片资源；显示用户选择的图片 URI。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Object item = imageList.get(position);
        if (item instanceof Integer) {
            holder.imageView.setImageResource((Integer) item);
        } else if (item instanceof String) {
            try {
                holder.imageView.setImageURI(Uri.parse((String) item));
            } catch (Exception e) {
                holder.imageView.setImageResource(R.drawable.ic_product_placeholder);
            }
        }
    }

    /**
     * 项目职责：返回商品图片轮播 Adapter当前列表需要展示的条目数量。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    /**
     * 项目职责：ImageViewHolder 对应的项目组件。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}
