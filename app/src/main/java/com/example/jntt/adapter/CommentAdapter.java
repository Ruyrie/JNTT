package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Comment;
import java.util.List;

/** 评论列表适配器（抖音 / 小红书风格） */
/**
 * 项目职责：文章评论 Adapter，负责评论内容、点赞状态和删除入口展示。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.VH> {

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
        void onDelete(Comment comment);
    }

    private final List<Comment> data;
    private final String currentUser;
    private final String articleAuthor;
    private final DataManager dm;
    private OnDeleteListener deleteListener;

    /**
     * 项目职责：创建文章评论 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public CommentAdapter(List<Comment> data, String currentUser,
            String articleAuthor, DataManager dm) {
        this.data = data;
        this.currentUser = currentUser;
        this.articleAuthor = articleAuthor;
        this.dm = dm;
    }

    /**
     * 项目职责：文章评论 Adapter，负责评论内容、点赞状态和删除入口展示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setOnDeleteListener(OnDeleteListener l) {
        this.deleteListener = l;
    }

    /**
     * 项目职责：为文章评论 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new VH(v);
    }

    /**
     * 项目职责：把当前位置的数据绑定到文章评论 Adapter的 item 布局控件上。
     * 关键调用：显示用户选择的图片 URI。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Comment c = data.get(position);

        // Avatar initial
        String initial = "U";
        if (c.nickname != null && !c.nickname.isEmpty()) {
            initial = String.valueOf(c.nickname.charAt(0)).toUpperCase();
        } else if (c.username != null && !c.username.isEmpty()) {
            initial = String.valueOf(c.username.charAt(0)).toUpperCase();
        }
        h.tvAvatar.setText(initial);

        if (c.avatarUri != null) {
            try {
                if (c.avatarUri.startsWith("data:image")) {
                    com.example.jntt.utils.ImageUtils.setAvatarFromBase64(h.ivAvatarImg, c.avatarUri);
                } else {
                    h.ivAvatarImg.setImageURI(android.net.Uri.parse(c.avatarUri));
                }
                h.ivAvatarImg.setVisibility(View.VISIBLE);
                h.tvAvatar.setVisibility(View.GONE);
            } catch (Exception e) {
                h.ivAvatarImg.setVisibility(View.GONE);
                h.tvAvatar.setVisibility(View.VISIBLE);
            }
        } else {
            h.ivAvatarImg.setVisibility(View.GONE);
            h.tvAvatar.setVisibility(View.VISIBLE);
        }

        h.tvUsername.setText(c.nickname != null && !c.nickname.isEmpty() ? c.nickname : c.username);
        h.tvContent.setText(c.content);
        h.tvTime.setText(c.time);
        h.tvLikeCount.setText(c.likeCount > 0 ? String.valueOf(c.likeCount) : "");

        // Delete button: visible for own comment OR article author
        boolean canDelete = c.username.equals(currentUser) || articleAuthor.equals(currentUser);
        h.tvDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);

        // Like state
        applyLikeState(h, c.isLikedByMe);

        // Like click
        h.layoutLike.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(v.getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentUser.equals(c.username)) {
                Toast.makeText(v.getContext(), "不能给自己的评论点赞", Toast.LENGTH_SHORT).show();
                return;
            }
            if (c.isLikedByMe) {
                dm.unlikeComment(currentUser, c.id);
                c.likeCount = Math.max(0, c.likeCount - 1);
                c.isLikedByMe = false;
            } else {
                dm.likeComment(currentUser, c.id);
                c.likeCount++;
                c.isLikedByMe = true;
            }
            applyLikeState(h, c.isLikedByMe);
            h.tvLikeCount.setText(c.likeCount > 0 ? String.valueOf(c.likeCount) : "");
        });

        // Delete click
        h.tvDelete.setOnClickListener(v -> {
            if (deleteListener != null)
                deleteListener.onDelete(c);
        });
    }

    /**
     * 项目职责：文章评论 Adapter，负责评论内容、点赞状态和删除入口展示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    private void applyLikeState(VH h, boolean liked) {
        if (liked) {
            h.ivLikeBtn.setImageResource(R.mipmap.dianzan);
        } else {
            h.ivLikeBtn.setImageResource(R.mipmap.weidianzan);
        }
    }

    /**
     * 项目职责：返回文章评论 Adapter当前列表需要展示的条目数量。
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
        TextView tvAvatar, tvUsername, tvContent, tvTime, tvDelete;
        ImageView ivLikeBtn;
        TextView tvLikeCount;
        LinearLayout layoutLike;
        android.widget.ImageView ivAvatarImg;

        VH(View v) {
            super(v);
            tvAvatar = v.findViewById(R.id.tvCommentAvatar);
            ivAvatarImg = v.findViewById(R.id.ivCommentAvatar);
            tvUsername = v.findViewById(R.id.tvCommentUsername);
            tvContent = v.findViewById(R.id.tvCommentContent);
            tvTime = v.findViewById(R.id.tvCommentTime);
            tvDelete = v.findViewById(R.id.tvDeleteComment);
            ivLikeBtn = v.findViewById(R.id.tvCommentLikeBtn);
            tvLikeCount = v.findViewById(R.id.tvCommentLikeCount);
            layoutLike = v.findViewById(R.id.layoutCommentLike);
        }
    }
}
