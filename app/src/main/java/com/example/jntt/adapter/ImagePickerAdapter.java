package com.example.jntt.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import java.util.List;

/**
 * 项目职责：图片选择 Adapter，负责发布商品/评价时展示已选图片和添加按钮。
 * 技术说明：绑定布局控件；绑定点击事件。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.PickerViewHolder> {

    private List<Uri> imageUris;
    private int maxImages;
    private OnImagePickerClickListener listener;

    /**
     * 项目职责：图片选择回调接口，负责把添加/删除图片操作交给发布或编辑页面处理。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnImagePickerClickListener {
        /**
         * 项目职责：把图片选择器中的添加图片点击回调给宿主页面打开拍照/相册。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onAddClick();

        /**
         * 项目职责：把图片或条目删除点击回调给宿主页面移除数据并刷新列表。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onDeleteClick(int position);
    }

    /**
     * 项目职责：创建图片选择 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public ImagePickerAdapter(List<Uri> imageUris, int maxImages, OnImagePickerClickListener listener) {
        this.imageUris = imageUris;
        this.maxImages = maxImages;
        this.listener = listener;
    }

    /**
     * 项目职责：为图片选择 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public PickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image_picker, parent, false);
        return new PickerViewHolder(view);
    }

    /**
     * 项目职责：把当前位置的数据绑定到图片选择 Adapter的 item 布局控件上。
     * 关键调用：显示用户选择的图片 URI。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull PickerViewHolder holder, int position) {
        if (position == imageUris.size()) {
            // Add button
            holder.llAddPlaceholder.setVisibility(View.VISIBLE);
            holder.ivPickerImage.setVisibility(View.GONE);
            holder.ivDeleteImage.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onAddClick();
            });
        } else {
            // Image item
            holder.llAddPlaceholder.setVisibility(View.GONE);
            holder.ivPickerImage.setVisibility(View.VISIBLE);
            holder.ivDeleteImage.setVisibility(View.VISIBLE);

            holder.ivPickerImage.setImageURI(imageUris.get(position));

            holder.ivDeleteImage.setOnClickListener(v -> {
                if (listener != null)
                    listener.onDeleteClick(position);
            });
            holder.itemView.setOnClickListener(null);
        }
    }

    /**
     * 项目职责：返回图片选择 Adapter当前列表需要展示的条目数量。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public int getItemCount() {
        return Math.min(imageUris.size() + 1, maxImages);
    }

    /**
     * 项目职责：PickerViewHolder 对应的项目组件。
     * 技术说明：绑定布局控件。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    static class PickerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPickerImage;
        ImageView ivDeleteImage;
        LinearLayout llAddPlaceholder;

        PickerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPickerImage = itemView.findViewById(R.id.ivPickerImage);
            ivDeleteImage = itemView.findViewById(R.id.ivDeleteImage);
            llAddPlaceholder = itemView.findViewById(R.id.llAddPlaceholder);
        }
    }
}
