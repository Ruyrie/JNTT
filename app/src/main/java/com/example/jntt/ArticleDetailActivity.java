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

/**
 * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
 * 技术说明：显示或隐藏软键盘；绑定布局控件；绑定点击事件；刷新列表。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private int articleId;
    private Article article;
    private DataManager dm;
    private String currentUser;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;

    // Views
    private TextView tvLikeCount, tvCommentCount, tvCommentCountBar;
    private ImageView ivLikeBtn;
    private LinearLayout llNoComments;
    private NestedScrollView nestedScroll;

    /**
     * 项目职责：初始化文章详情页，负责文章展示、点赞、评论提交和删除，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：读写本地业务数据；页面跳转或传递参数。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
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

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
     * 关键调用：绑定布局控件；绑定点击事件；提示用户操作结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
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

        // Cover image is intentionally not shown in detail view
        // (the same image already appears in the article body via rvArticleImages)

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
                /**
                 * 项目职责：为文章详情页创建 RecyclerView 列表项 ViewHolder。
                 * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                 * 配合代码：配合当前模块的布局、数据类和调用方使用。
                 */
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

                /**
                 * 项目职责：把当前位置的数据绑定到文章详情页的 item 布局控件上。
                 * 关键调用：显示内置图片资源。
                 * 配合代码：配合当前模块的布局、数据类和调用方使用。
                 */
                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    ((ImageView) holder.itemView).setImageResource(internalImages.get(position));
                }

                /**
                 * 项目职责：返回文章详情页当前列表需要展示的条目数量。
                 * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                 * 配合代码：配合当前模块的布局、数据类和调用方使用。
                 */
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

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void refreshFollowBtn(TextView btn) {
        boolean following = dm.isFollowing(currentUser, article.author);
        btn.setText(following ? "已关注" : "+ 关注");
        btn.setTextColor(following ? 0xFF999999 : 0xFF2F80ED);
    }

    // ─── Comments ────────────────────────────────────────────────────────────

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
     * 关键调用：绑定布局控件；绑定点击事件；刷新列表。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
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

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void updateCommentCountUI() {
        int n = comments.size();
        tvCommentCount.setText(n + " 条");
        tvCommentCountBar.setText(String.valueOf(n));
        llNoComments.setVisibility(n == 0 ? View.VISIBLE : View.GONE);
    }

    // ─── Bottom bar (like + comment input) ───────────────────────────────────

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
     * 关键调用：绑定布局控件；绑定点击事件；提示用户操作结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void setupBottomBar() {
        ivLikeBtn = findViewById(R.id.tvLikeBtn);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        refreshLikeUI();

        // Like toggle
        findViewById(R.id.layoutLike).setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentUser.equals(article.author)) {
                Toast.makeText(this, "不能给自己的文章点赞", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean nowLiked = dm.isArticleLiked(currentUser, articleId);
            if (nowLiked) {
                dm.unlikeArticle(currentUser, articleId);
            } else {
                dm.likeArticle(currentUser, articleId);
                // Heart-beat animation
                ivLikeBtn.animate().scaleX(1.35f).scaleY(1.35f).setDuration(130)
                        .withEndAction(() -> ivLikeBtn.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
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

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void refreshLikeUI() {
        boolean liked = currentUser != null && dm.isArticleLiked(currentUser, articleId);
        int count = dm.getArticleLikeCount(articleId);
        ivLikeBtn.setImageResource(liked ? R.mipmap.dianzan : R.mipmap.weidianzan);
        tvLikeCount.setText(count > 0 ? String.valueOf(count) : "");
    }

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除。
     * 关键调用：显示或隐藏软键盘；提示用户操作结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
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

    /**
     * 项目职责：文章详情页，负责文章展示、点赞、评论提交和删除回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh like count in case user navigated away and back
        if (ivLikeBtn != null)
            refreshLikeUI();
    }
}
