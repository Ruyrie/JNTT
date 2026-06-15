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

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.VH> {

    public interface OnItemClickListener {
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
     * Lightweight constructor — no like/comment features (MyArticles, MyFavorites).
     */
    public ArticleAdapter(List<Article> data, OnItemClickListener listener) {
        this(data, listener, null, null);
    }

    /**
     * Full-featured constructor — enables live like counts and comment preview
     * (Headlines feed).
     */
    public ArticleAdapter(List<Article> data, OnItemClickListener listener,
            DataManager dm, String currentUser) {
        this.data = data;
        this.listener = listener;
        this.dm = dm;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new VH(v);
    }

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

    private void refreshItemLike(VH h, Article a) {
        boolean liked = dm.isArticleLiked(currentUser, a.id);
        int count = dm.getArticleLikeCount(a.id);
        h.ivItemLikeIcon.setImageResource(liked ? R.mipmap.dianzan : R.mipmap.weidianzan);
        h.tvItemLikeCount.setText(count > 0 ? String.valueOf(count) : "");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

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
