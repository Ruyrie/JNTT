package com.example.jntt.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.ProductComment;
import java.util.Arrays;
import java.util.List;

/**
 * 项目职责：商品评价 Adapter，负责评价内容、图片和删除本人评价入口展示。
 * 技术说明：绑定布局控件；绑定点击事件。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class ProductCommentAdapter extends RecyclerView.Adapter<ProductCommentAdapter.CommentViewHolder> {

    private List<ProductComment> data;
    private String currentUser;
    private OnDeleteListener deleteListener;

    /**
     * 项目职责：评论删除回调接口，负责把评论条目的删除点击交给详情页或评价页确认。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnDeleteListener {
        /**
         * 项目职责：把评论或商品条目的删除点击回调给宿主页面确认。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onDelete(ProductComment comment);
    }

    /**
     * 项目职责：创建商品评价 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public ProductCommentAdapter(List<ProductComment> data, String currentUser) {
        this.data = data;
        this.currentUser = currentUser;
    }

    /**
     * 项目职责：商品评价 Adapter，负责评价内容、图片和删除本人评价入口展示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setOnDeleteListener(OnDeleteListener listener) {
        this.deleteListener = listener;
    }

    /**
     * 项目职责：为商品评价 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_comment, parent, false);
        return new CommentViewHolder(view);
    }

    /**
     * 项目职责：把当前位置的数据绑定到商品评价 Adapter的 item 布局控件上。
     * 关键调用：显示内置图片资源；显示用户选择的图片 URI；连接 RecyclerView 与 Adapter；设置列表排列方式。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        ProductComment c = data.get(position);

        holder.tvUsername.setText(c.nickname != null && !c.nickname.isEmpty() ? c.nickname : c.username);
        holder.tvTime.setText(c.time);
        holder.tvContent.setText(c.content);

        if (c.avatarUri != null && !c.avatarUri.isEmpty()) {
            try {
                holder.ivAvatar.setImageURI(Uri.parse(c.avatarUri));
            } catch (Exception e) {
                holder.ivAvatar.setImageResource(R.mipmap.ic_launcher_round);
            }
        } else {
            holder.ivAvatar.setImageResource(R.mipmap.ic_launcher_round);
        }

        if (c.username.equals(currentUser) || "admin".equals(currentUser)) {
            holder.tvDelete.setVisibility(View.VISIBLE);
            holder.tvDelete.setOnClickListener(v -> {
                if (deleteListener != null)
                    deleteListener.onDelete(c);
            });
        } else {
            holder.tvDelete.setVisibility(View.GONE);
        }

        if (c.images != null && !c.images.isEmpty()) {
            holder.rvCommentImages.setVisibility(View.VISIBLE);
            holder.rvCommentImages.setLayoutManager(
                    new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));

            List<String> uris = Arrays.asList(c.images.split(","));
            holder.rvCommentImages.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                /**
                 * 项目职责：为商品评价 Adapter创建 RecyclerView 列表项 ViewHolder。
                 * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                 * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
                 */
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    ImageView iv = new ImageView(parent.getContext());
                    int size = (int) (100 * parent.getContext().getResources().getDisplayMetrics().density);
                    ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
                    params.setMarginEnd((int) (8 * parent.getContext().getResources().getDisplayMetrics().density));
                    iv.setLayoutParams(params);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setBackgroundColor(0xFFEEEEEE);
                    return new RecyclerView.ViewHolder(iv) {
                    };
                }

                /**
                 * 项目职责：把当前位置的数据绑定到商品评价 Adapter的 item 布局控件上。
                 * 关键调用：显示内置图片资源；显示用户选择的图片 URI。
                 * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
                 */
                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderImg, int position) {
                    ImageView iv = (ImageView) holderImg.itemView;
                    try {
                        iv.setImageURI(Uri.parse(uris.get(position)));
                    } catch (Exception e) {
                        iv.setImageResource(R.drawable.ic_launcher_background);
                    }
                }

                /**
                 * 项目职责：返回商品评价 Adapter当前列表需要展示的条目数量。
                 * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                 * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
                 */
                @Override
                public int getItemCount() {
                    return uris.size();
                }
            });
        } else {
            holder.rvCommentImages.setVisibility(View.GONE);
        }
    }

    /**
     * 项目职责：返回商品评价 Adapter当前列表需要展示的条目数量。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 项目职责：CommentViewHolder 对应的项目组件。
     * 技术说明：绑定布局控件。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvTime;
        TextView tvDelete;
        TextView tvContent;
        RecyclerView rvCommentImages;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            tvContent = itemView.findViewById(R.id.tvContent);
            rvCommentImages = itemView.findViewById(R.id.rvCommentImages);
        }
    }
}
