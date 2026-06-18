package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.User;
import java.util.List;

/**
 * 项目职责：账号 Adapter，负责账号管理列表展示和点击/长按回调。
 * 技术说明：绑定布局控件；绑定点击事件。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {

    /**
     * 项目职责：OnItemListener 对应的项目组件。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnItemListener {
        /**
         * 项目职责：账号 Adapter，负责账号管理列表展示和点击/长按回调。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onShortClick(User user);

        /**
         * 项目职责：账号 Adapter，负责账号管理列表展示和点击/长按回调。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onLongClick(User user);
    }

    private final List<User> data;
    private final OnItemListener listener;

    /**
     * 项目职责：创建账号列表 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public UserAdapter(List<User> data, OnItemListener listener) {
        this.data = data;
        this.listener = listener;
    }

    /**
     * 项目职责：为账号列表 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new VH(v);
    }

    /**
     * 项目职责：把当前位置的数据绑定到账号列表 Adapter的 item 布局控件上。
     * 关键调用：显示用户选择的图片 URI。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        User u = data.get(position);
        holder.tvUsername.setText(u.username);
        // 首字母头像
        String initial = u.username.length() > 0
                ? String.valueOf(u.username.charAt(0)).toUpperCase()
                : "?";
        holder.tvInitial.setText(initial);

        if (u.avatarUri != null) {
            try {
                if (u.avatarUri.startsWith("data:image")) {
                    com.example.jntt.utils.ImageUtils.setAvatarFromBase64(holder.ivAvatar, u.avatarUri);
                } else {
                    holder.ivAvatar.setImageURI(android.net.Uri.parse(u.avatarUri));
                }
                holder.ivAvatar.setVisibility(View.VISIBLE);
                holder.tvInitial.setVisibility(View.GONE);
            } catch (Exception e) {
                holder.ivAvatar.setVisibility(View.GONE);
                holder.tvInitial.setVisibility(View.VISIBLE);
            }
        } else {
            holder.ivAvatar.setVisibility(View.GONE);
            holder.tvInitial.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> listener.onShortClick(u));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(u);
            return true;
        });
    }

    /**
     * 项目职责：返回账号列表 Adapter当前列表需要展示的条目数量。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 项目职责：VH 对应的项目组件。
     * 技术说明：绑定布局控件。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView tvUsername, tvInitial;
        ImageView ivAvatar;

        VH(View v) {
            super(v);
            tvUsername = v.findViewById(R.id.tvUsername);
            tvInitial = v.findViewById(R.id.tvUserInitial);
            ivAvatar = v.findViewById(R.id.ivUserAvatar);
        }
    }
}
