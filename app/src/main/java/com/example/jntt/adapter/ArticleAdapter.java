package com.example.jntt.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import com.example.jntt.model.Comment;
import java.util.List;

/**
 * 项目职责：文章 Adapter，负责文章卡片展示、点赞收藏状态和详情入口。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.VH> {

    /**
     * 项目职责：商品卡片点击回调接口，负责把 ProductAdapter 中的商品点击交给 MallFragment 打开详情页。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnItemClickListener {
        /**
         * 项目职责：把商品/文章/账号等列表项点击回调给页面处理详情跳转。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onItemClick(Article article);
    }

    private final List<Article> data;
    private final OnItemClickListener listener;
    private final DataManager dm; // null in non-feed contexts
    private final String currentUser; // null if not using dm

    private static final int[] THUMB_COLORS = {
            0xFFE8F5E9, 0xFFFFF3E0, 0xFFE3F2FD, 0xFFFCE4EC,
            0xFFF3E5F5, 0xFFE0F2F1, 0xFFFFF8E1
    };

    /**
     * 项目职责：创建文章列表 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public ArticleAdapter(List<Article> data, OnItemClickListener listener) {
        this(data, listener, null, null);
    }

    /**
     * 项目职责：创建文章列表 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public ArticleAdapter(List<Article> data, OnItemClickListener listener,
            DataManager dm, String currentUser) {
        this.data = data;
        this.listener = listener;
        this.dm = dm;
        this.currentUser = currentUser;
    }

    /**
     * 项目职责：为文章列表 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new VH(v);
    }

    /**
     * 项目职责：把当前位置的数据绑定到文章列表 Adapter的 item 布局控件上。
     * 关键调用：显示内置图片资源；显示用户选择的图片 URI。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Article a = data.get(position);

        // Author row
        String authorName = a.authorNickname != null && !a.authorNickname.isEmpty() ? a.authorNickname : a.author;
        String initial = (authorName != null && !authorName.isEmpty())
                ? String.valueOf(authorName.charAt(0)).toUpperCase()
                : "农";
        h.tvAuthorInitial.setText(initial);

        if (a.authorAvatarUri != null) {
            try {
                if (a.authorAvatarUri.startsWith("data:image")) {
                    com.example.jntt.utils.ImageUtils.setAvatarFromBase64(h.ivAuthorAvatar, a.authorAvatarUri);
                } else {
                    h.ivAuthorAvatar.setImageURI(android.net.Uri.parse(a.authorAvatarUri));
                }
                h.ivAuthorAvatar.setVisibility(View.VISIBLE);
                h.tvAuthorInitial.setVisibility(View.GONE);
            } catch (Exception e) {
                h.ivAuthorAvatar.setVisibility(View.GONE);
                h.tvAuthorInitial.setVisibility(View.VISIBLE);
            }
        } else {
            h.ivAuthorAvatar.setVisibility(View.GONE);
            h.tvAuthorInitial.setVisibility(View.VISIBLE);
        }

        h.tvAuthor.setText(authorName);
        h.tvTime.setText(a.time);
        h.tvReadCount.setText("阅读 " + a.readCount);

        h.tvArticleTitle.setText(a.title);
        if (a.content != null && !a.content.trim().isEmpty()) {
            h.tvArticleContent.setText(a.content);
            h.tvArticleContent.setVisibility(View.VISIBLE);
        } else {
            h.tvArticleContent.setVisibility(View.GONE);
        }

        // Cover image
        if (a.coverUri != null) {
            h.ivThumb.setImageURI(Uri.parse(a.coverUri));
        } else {
            switch (a.id) {
                case 5: // admin
                    h.ivThumb.setImageResource(R.mipmap.text1);
                    break;
                case 4: // user1
                    h.ivThumb.setImageResource(R.mipmap.text2);
                    break;
                case 3: // user2
                    h.ivThumb.setImageResource(R.mipmap.text3);
                    break;
                case 2: // user3
                    h.ivThumb.setImageResource(R.mipmap.text4);
                    break;
                case 1: // user4
                    h.ivThumb.setImageResource(R.mipmap.text5);
                    break;
                default:
                    h.ivThumb.setBackgroundColor(THUMB_COLORS[position % THUMB_COLORS.length]);
                    h.ivThumb.setImageResource(R.drawable.ic_product_placeholder);
            }
        }

        // ── Instagram-style like + comment (only when DM is available) ──
        if (dm != null && currentUser != null && !a.isDeleted) {
            bindSocialRow(h, a);
        } else {
            // Minimal mode or deleted: hide counts + preview
            h.layoutTopComment.setVisibility(View.GONE);
            h.tvItemLikeCount.setText("");
            h.tvItemCommentCount.setText("");
            h.ivItemLikeIcon.setImageResource(R.mipmap.weidianzan);
        }

        // Deleted UI
        if (a.isDeleted) {
            h.flDeletedOverlay.setVisibility(View.VISIBLE);
            h.tvArticleTitle.setVisibility(View.INVISIBLE);
            h.tvArticleContent.setVisibility(View.INVISIBLE);
        } else {
            h.flDeletedOverlay.setVisibility(View.GONE);
            h.tvArticleTitle.setVisibility(View.VISIBLE);
            if (a.content != null && !a.content.trim().isEmpty()) {
                h.tvArticleContent.setVisibility(View.VISIBLE);
            } else {
                h.tvArticleContent.setVisibility(View.GONE);
            }
        }

        h.itemView.setOnClickListener(v -> {
            if (a.isDeleted) {
                android.widget.Toast.makeText(v.getContext(), "该稿件已被删除", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onItemClick(a);
        });
    }

    /**
     * 项目职责：文章 Adapter，负责文章卡片展示、点赞收藏状态和详情入口。
     * 关键调用：绑定点击事件；提示用户操作结果。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    private void bindSocialRow(VH h, Article a) {
        // Like icon + count
        refreshItemLike(h, a);

        // Like toggle in feed
        h.layoutItemLike.setOnClickListener(v -> {
            // Intercept: don't bubble up to item click
            v.setPressed(true);
            if (currentUser.equals(a.author)) {
                android.widget.Toast.makeText(v.getContext(), "不能给自己的文章点赞", android.widget.Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (dm.isArticleLiked(currentUser, a.id)) {
                dm.unlikeArticle(currentUser, a.id);
            } else {
                dm.likeArticle(currentUser, a.id);
                // Heart-beat
                h.ivItemLikeIcon.animate()
                        .scaleX(1.35f).scaleY(1.35f).setDuration(120)
                        .withEndAction(() -> h.ivItemLikeIcon.animate()
                                .scaleX(1f).scaleY(1f).setDuration(90).start())
                        .start();
            }
            refreshItemLike(h, a);
        });

        // Comment count
        int commentCount = dm.getCommentCount(a.id);
        h.tvItemCommentCount.setText(commentCount > 0 ? String.valueOf(commentCount) : "");

        // Top comment preview
        Comment top = dm.getTopComment(a.id);
        if (top != null) {
            h.tvTopCommentUsername
                    .setText(top.nickname != null && !top.nickname.isEmpty() ? top.nickname : top.username);
            h.tvTopCommentContent.setText(top.content);
            h.layoutTopComment.setVisibility(View.VISIBLE);
        } else {
            h.layoutTopComment.setVisibility(View.GONE);
        }
    }

    /**
     * 项目职责：文章 Adapter，负责文章卡片展示、点赞收藏状态和详情入口。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    private void refreshItemLike(VH h, Article a) {
        boolean liked = dm.isArticleLiked(currentUser, a.id);
        int count = dm.getArticleLikeCount(a.id);
        h.ivItemLikeIcon.setImageResource(liked ? R.mipmap.dianzan : R.mipmap.weidianzan);
        h.tvItemLikeCount.setText(count > 0 ? String.valueOf(count) : "");
    }

    /**
     * 项目职责：返回文章列表 Adapter当前列表需要展示的条目数量。
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
        TextView tvAuthorInitial, tvAuthor, tvTime, tvReadCount, tvArticleTitle, tvArticleContent;
        TextView tvItemLikeCount, tvItemCommentCount;
        TextView tvTopCommentUsername, tvTopCommentContent;
        ImageView ivThumb, ivItemLikeIcon, ivAuthorAvatar;
        LinearLayout layoutItemLike, layoutTopComment;
        FrameLayout flDeletedOverlay;

        VH(View v) {
            super(v);
            tvAuthorInitial = v.findViewById(R.id.tvAuthorInitial);
            ivAuthorAvatar = v.findViewById(R.id.ivAuthorAvatar);
            tvAuthor = v.findViewById(R.id.tvArticleAuthor);
            tvTime = v.findViewById(R.id.tvArticleTime);
            tvReadCount = v.findViewById(R.id.tvArticleReadCount);
            tvArticleTitle = v.findViewById(R.id.tvArticleTitle);
            tvArticleContent = v.findViewById(R.id.tvArticleContent);
            ivThumb = v.findViewById(R.id.ivArticleThumb);
            ivItemLikeIcon = v.findViewById(R.id.ivItemLikeIcon);
            tvItemLikeCount = v.findViewById(R.id.tvItemLikeCount);
            tvItemCommentCount = v.findViewById(R.id.tvItemCommentCount);
            layoutItemLike = v.findViewById(R.id.layoutItemLike);
            tvTopCommentUsername = v.findViewById(R.id.tvTopCommentUsername);
            tvTopCommentContent = v.findViewById(R.id.tvTopCommentContent);
            layoutTopComment = v.findViewById(R.id.layoutTopComment);
            flDeletedOverlay = v.findViewById(R.id.flDeletedOverlay);
        }
    }
}
