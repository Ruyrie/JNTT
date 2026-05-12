package com.example.jntt;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.CommentAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import com.example.jntt.model.Comment;
import java.util.List;

public class ArticleDetailActivity extends AppCompatActivity {

    private int articleId;
    private Article article;
    private DataManager dm;
    private String currentUser;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;

    // Views
    private TextView tvLikeBtn, tvLikeCount, tvCommentCount, tvCommentCountBar;
    private LinearLayout llNoComments;
    private NestedScrollView nestedScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        dm = DataManager.getInstance(this);
        currentUser = dm.getLoggedUser();
        articleId = getIntent().getIntExtra("article_id", -1);

        dm.incrementReadCount(articleId);

        for (Article a : dm.getArticles()) {
            if (a.id == articleId) {
                article = a;
                break;
            }
        }
        if (article == null) {
            finish();
            return;
        }

        bindViews();
        setupComments();
        setupBottomBar();
    }

    // ─── Article binding ─────────────────────────────────────────────────────

    private void bindViews() {
        nestedScroll = findViewById(R.id.nestedScroll);

        // Toolbar
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(article.title);
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        TextView tvDelete = findViewById(R.id.tvDeleteBtn);
        if (currentUser != null && article.author.equals(currentUser)) {
            tvDelete.setVisibility(View.VISIBLE);
            tvDelete.setOnClickListener(v -> {
                android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
                android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
                tvTitle.setText("删除作品");
                tvMessage.setText("确定要删除这篇稿件吗？删除后其他人将无法查看。");

                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(view)
                        .create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                android.widget.TextView btnConfirm = view.findViewById(R.id.btnDialogConfirm);
                btnConfirm.setText("删除");
                btnConfirm.setBackgroundResource(R.drawable.bg_auth_button);

                view.findViewById(R.id.btnDialogCancel).setOnClickListener(btn -> dialog.dismiss());
                btnConfirm.setOnClickListener(btn -> {
                    dialog.dismiss();
                    dm.deleteArticle(articleId);
                    Toast.makeText(this, "作品已删除", Toast.LENGTH_SHORT).show();
                    finish();
                });
                dialog.show();
            });
        }

        // Cover
        ImageView ivCover = findViewById(R.id.ivDetailCover);
        if (article.coverUri != null) {
            try {
                ivCover.setImageURI(Uri.parse(article.coverUri));
                ivCover.setVisibility(View.VISIBLE);
            } catch (Exception ignored) {
            }
        } else {
            // Hardcode cover for seed articles
            switch (article.id) {
                case 5: // admin
                    ivCover.setImageResource(R.mipmap.text1);
                    ivCover.setVisibility(View.VISIBLE);
                    break;
                case 4: // user1
                    ivCover.setImageResource(R.mipmap.text2);
                    ivCover.setVisibility(View.VISIBLE);
                    break;
                case 3: // user2
                    ivCover.setImageResource(R.mipmap.text3);
                    ivCover.setVisibility(View.VISIBLE);
                    break;
                case 2: // user3
                    ivCover.setImageResource(R.mipmap.text4);
                    ivCover.setVisibility(View.VISIBLE);
                    break;
                case 1: // user4
                    ivCover.setImageResource(R.mipmap.text5);
                    ivCover.setVisibility(View.VISIBLE);
                    break;
                default:
                    ivCover.setVisibility(View.GONE);
            }
        }

        // Title + content
        ((TextView) findViewById(R.id.tvDetailTitle)).setText(article.title);
        ((TextView) findViewById(R.id.tvDetailContent)).setText(article.content);
        ((TextView) findViewById(R.id.tvDetailReadCount)).setText("阅读 " + article.readCount + " 次");

        // Internal Images for Seed Articles
        RecyclerView rvArticleImages = findViewById(R.id.rvArticleImages);
        rvArticleImages.setLayoutManager(new LinearLayoutManager(this));
        java.util.List<Integer> internalImages = new java.util.ArrayList<>();
        if (article.coverUri == null) {
            switch (article.id) {
                case 5: // admin
                    internalImages.add(R.mipmap.text1);
                    break;
                case 4: // user1
                    internalImages.add(R.mipmap.text2);
                    break;
                case 3: // user2
                    internalImages.add(R.mipmap.text3);
                    break;
                case 2: // user3
                    internalImages.add(R.mipmap.text4);
                    break;
                case 1: // user4
                    internalImages.add(R.mipmap.text5);
                    break;
            }
        }

        if (!internalImages.isEmpty()) {
            rvArticleImages.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent,
                        int viewType) {
                    ImageView iv = new ImageView(parent.getContext());
                    iv.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                    iv.setAdjustViewBounds(true);
                    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    iv.setPadding(0, 0, 0, 24); // Add some bottom padding
                    return new RecyclerView.ViewHolder(iv) {
                    };
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    ((ImageView) holder.itemView).setImageResource(internalImages.get(position));
                }

                @Override
                public int getItemCount() {
                    return internalImages.size();
                }
            });
            rvArticleImages.setVisibility(View.VISIBLE);
        } else {
            rvArticleImages.setVisibility(View.GONE);
        }

        // Author avatar initial
        String authorName = article.authorNickname != null && !article.authorNickname.isEmpty() ? article.authorNickname
                : article.author;
        String initial = (authorName != null && !authorName.isEmpty())
                ? String.valueOf(authorName.charAt(0)).toUpperCase()
                : "A";
        TextView tvAuthorInitial = findViewById(R.id.tvDetailAuthorInitial);
        ImageView ivAuthorAvatar = findViewById(R.id.ivDetailAuthorAvatar);
        tvAuthorInitial.setText(initial);

        if (article.authorAvatarUri != null) {
            try {
                if (article.authorAvatarUri.startsWith("data:image")) {
                    com.example.jntt.utils.ImageUtils.setAvatarFromBase64(ivAuthorAvatar, article.authorAvatarUri);
                } else {
                    ivAuthorAvatar.setImageURI(Uri.parse(article.authorAvatarUri));
                }
                ivAuthorAvatar.setVisibility(View.VISIBLE);
                tvAuthorInitial.setVisibility(View.GONE);
            } catch (Exception e) {
                ivAuthorAvatar.setVisibility(View.GONE);
                tvAuthorInitial.setVisibility(View.VISIBLE);
            }
        } else {
            ivAuthorAvatar.setVisibility(View.GONE);
            tvAuthorInitial.setVisibility(View.VISIBLE);
        }

        ((TextView) findViewById(R.id.tvDetailAuthor)).setText(authorName);
        ((TextView) findViewById(R.id.tvDetailTime)).setText(article.time);

        // Follow button (hidden if viewing own article)
        TextView tvFollow = findViewById(R.id.tvFollowBtn);
        if (currentUser != null && !article.author.equals(currentUser)) {
            tvFollow.setVisibility(View.VISIBLE);
            refreshFollowBtn(tvFollow);
            tvFollow.setOnClickListener(v -> {
                if (dm.isFollowing(currentUser, article.author)) {
                    dm.unfollowUser(currentUser, article.author);
                } else {
                    dm.followUser(currentUser, article.author);
                }
                refreshFollowBtn(tvFollow);
            });
        }
    }

    private void refreshFollowBtn(TextView btn) {
        boolean following = dm.isFollowing(currentUser, article.author);
        btn.setText(following ? "已关注" : "+ 关注");
        btn.setTextColor(following ? 0xFF999999 : 0xFF2F80ED);
    }

    // ─── Comments ────────────────────────────────────────────────────────────

    private void setupComments() {
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvCommentCountBar = findViewById(R.id.tvCommentCountBar);
        llNoComments = findViewById(R.id.llNoComments);

        comments = dm.getComments(articleId, currentUser != null ? currentUser : "");
        updateCommentCountUI();

        commentAdapter = new CommentAdapter(comments, currentUser, article.author, dm);
        commentAdapter.setOnDeleteListener(comment -> {
            android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
            android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
            tvTitle.setText("删除评论");
            tvMessage.setText("确定删除这条评论吗？");

            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(view)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            android.widget.TextView btnConfirm = view.findViewById(R.id.btnDialogConfirm);
            btnConfirm.setText("删除");
            btnConfirm.setBackgroundResource(R.drawable.bg_auth_button);

            view.findViewById(R.id.btnDialogCancel).setOnClickListener(btn -> dialog.dismiss());
            btnConfirm.setOnClickListener(btn -> {
                dialog.dismiss();
                dm.deleteComment(comment.id);
                comments.remove(comment);
                commentAdapter.notifyDataSetChanged();
                updateCommentCountUI();
            });
            dialog.show();
        });

        RecyclerView rv = findViewById(R.id.rvComments);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(commentAdapter);
    }

    private void updateCommentCountUI() {
        int n = comments.size();
        tvCommentCount.setText(n + " 条");
        tvCommentCountBar.setText(String.valueOf(n));
        llNoComments.setVisibility(n == 0 ? View.VISIBLE : View.GONE);
    }

    // ─── Bottom bar (like + comment input) ───────────────────────────────────

    private void setupBottomBar() {
        tvLikeBtn = findViewById(R.id.tvLikeBtn);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        refreshLikeUI();

        // Like toggle
        findViewById(R.id.layoutLike).setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean nowLiked = dm.isArticleLiked(currentUser, articleId);
            if (nowLiked) {
                dm.unlikeArticle(currentUser, articleId);
            } else {
                dm.likeArticle(currentUser, articleId);
                // Heart-beat animation
                tvLikeBtn.animate().scaleX(1.35f).scaleY(1.35f).setDuration(130)
                        .withEndAction(() -> tvLikeBtn.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                        .start();
            }
            refreshLikeUI();
        });

        // Comment submit via keyboard "Send" action
        EditText etComment = findViewById(R.id.etComment);
        etComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitComment(etComment);
                return true;
            }
            return false;
        });

        // Scroll to comments when clicking the comment icon
        findViewById(R.id.layoutCommentIcon).setOnClickListener(v -> {
            if (tvCommentCount != null) {
                nestedScroll.post(() -> {
                    // Scroll to the comment header
                    nestedScroll.smoothScrollTo(0, ((View) tvCommentCount.getParent()).getTop());
                });
            }
        });
    }

    private void refreshLikeUI() {
        boolean liked = currentUser != null && dm.isArticleLiked(currentUser, articleId);
        int count = dm.getArticleLikeCount(articleId);
        tvLikeBtn.setText(liked ? "♥" : "♡");
        tvLikeBtn.setTextColor(liked ? 0xFFE53935 : 0xFFAAAAAA);
        tvLikeCount.setText(count > 0 ? String.valueOf(count) : "");
    }

    private void submitComment(EditText et) {
        if (currentUser == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = et.getText().toString().trim();
        if (text.isEmpty())
            return;

        Comment c = dm.addComment(articleId, currentUser, text);
        c.nickname = dm.getNickname(currentUser);
        c.isLikedByMe = false;
        comments.add(c);
        commentAdapter.notifyItemInserted(comments.size() - 1);
        et.setText("");

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

        updateCommentCountUI();

        // Scroll to new comment
        nestedScroll.post(() -> nestedScroll.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh like count in case user navigated away and back
        if (tvLikeBtn != null)
            refreshLikeUI();
    }
}
