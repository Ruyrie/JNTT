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

    private int            articleId;
    private Article        article;
    private DataManager    dm;
    private String         currentUser;
    private List<Comment>  comments;
    private CommentAdapter commentAdapter;

    // Views
    private TextView       tvLikeBtn, tvLikeCount, tvCommentCount, tvCommentCountBar;
    private LinearLayout   llNoComments;
    private NestedScrollView nestedScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        dm          = DataManager.getInstance(this);
        currentUser = dm.getLoggedUser();
        articleId   = getIntent().getIntExtra("article_id", -1);

        dm.incrementReadCount(articleId);

        for (Article a : dm.getArticles()) {
            if (a.id == articleId) { article = a; break; }
        }
        if (article == null) { finish(); return; }

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

        // Cover
        ImageView ivCover = findViewById(R.id.ivDetailCover);
        if (article.coverUri != null) {
            try {
                ivCover.setImageURI(Uri.parse(article.coverUri));
                ivCover.setVisibility(View.VISIBLE);
            } catch (Exception ignored) {}
        }

        // Title + content
        ((TextView) findViewById(R.id.tvDetailTitle)).setText(article.title);
        ((TextView) findViewById(R.id.tvDetailContent)).setText(article.content);
        ((TextView) findViewById(R.id.tvDetailReadCount)).setText("阅读 " + article.readCount + " 次");

        // Author avatar initial
        String initial = (article.author != null && !article.author.isEmpty())
                ? String.valueOf(article.author.charAt(0)).toUpperCase() : "A";
        ((TextView) findViewById(R.id.tvDetailAuthorInitial)).setText(initial);
        ((TextView) findViewById(R.id.tvDetailAuthor)).setText(article.author);
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
        tvCommentCount    = findViewById(R.id.tvCommentCount);
        tvCommentCountBar = findViewById(R.id.tvCommentCountBar);
        llNoComments      = findViewById(R.id.llNoComments);

        comments = dm.getComments(articleId, currentUser != null ? currentUser : "");
        updateCommentCountUI();

        commentAdapter = new CommentAdapter(comments, currentUser, article.author, dm);
        commentAdapter.setOnDeleteListener(comment ->
            new AlertDialog.Builder(this)
                .setTitle("删除评论")
                .setMessage("确定删除这条评论吗？")
                .setPositiveButton("删除", (d, w) -> {
                    dm.deleteComment(comment.id);
                    comments.remove(comment);
                    commentAdapter.notifyDataSetChanged();
                    updateCommentCountUI();
                })
                .setNegativeButton("取消", null)
                .show());

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
        tvLikeBtn   = findViewById(R.id.tvLikeBtn);
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
                    .withEndAction(() ->
                        tvLikeBtn.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
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
    }

    private void refreshLikeUI() {
        boolean liked = currentUser != null && dm.isArticleLiked(currentUser, articleId);
        int count     = dm.getArticleLikeCount(articleId);
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
        if (text.isEmpty()) return;

        Comment c = dm.addComment(articleId, currentUser, text);
        c.isLikedByMe = false;
        comments.add(c);
        commentAdapter.notifyItemInserted(comments.size() - 1);
        et.setText("");

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

        updateCommentCountUI();

        // Scroll to new comment
        nestedScroll.post(() -> nestedScroll.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh like count in case user navigated away and back
        if (tvLikeBtn != null) refreshLikeUI();
    }
}
