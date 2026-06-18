package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.ArticleAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import java.util.List;

/** 我的文章界面：复用文章列表，仅显示当前账号发布的文章 */
/**
 * 项目职责：我的文章页，负责当前用户文章列表和详情跳转。
 * 技术说明：绑定布局控件；绑定点击事件。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class MyArticlesActivity extends AppCompatActivity {

    private RecyclerView rv;
    private LinearLayout llEmptyState;
    private TextView tvTitle, btnAddArticle, ivAddArticleIcon, tvEmptyArticleHint;
    private String targetUsername;
    private String currentUser;

    /**
     * 项目职责：初始化我的文章页，负责当前用户文章列表和详情跳转，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；读写本地业务数据；页面跳转或传递参数。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        rv = findViewById(R.id.rvArticles);
        llEmptyState = findViewById(R.id.llEmptyState);
        tvTitle = findViewById(R.id.tvArticleListTitle);
        btnAddArticle = findViewById(R.id.btnAddArticle);
        ivAddArticleIcon = findViewById(R.id.ivAddArticleIcon);
        tvEmptyArticleHint = findViewById(R.id.tvEmptyArticleHint);

        DataManager dm = DataManager.getInstance(this);
        currentUser = dm.getLoggedUser();
        targetUsername = getIntent().getStringExtra("username");
        if (targetUsername == null || targetUsername.isEmpty()) {
            targetUsername = currentUser;
        }

        boolean viewingOwnArticles = targetUsername != null && targetUsername.equals(currentUser);
        String nickname = dm.getNickname(targetUsername);
        String displayName = nickname != null && !nickname.isEmpty() ? nickname : targetUsername;
        tvTitle.setText(viewingOwnArticles ? "我的文章" : displayName + "的文章");
        tvEmptyArticleHint.setText(viewingOwnArticles ? "您还未上传过文章" : "该账号还未发布文章");

        View.OnClickListener goAdd = v -> startActivity(new Intent(this, AddArticleActivity.class));
        btnAddArticle.setOnClickListener(goAdd);
        ivAddArticleIcon.setOnClickListener(goAdd);

        btnAddArticle.setVisibility(viewingOwnArticles ? View.VISIBLE : View.GONE);
        ivAddArticleIcon.setVisibility(viewingOwnArticles ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 项目职责：我的文章页，负责当前用户文章列表和详情跳转回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * 项目职责：我的文章页，负责当前用户文章列表和详情跳转。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void loadData() {
        DataManager dm = DataManager.getInstance(this);
        List<Article> articles = dm.getArticlesByAuthor(targetUsername);

        if (articles == null || articles.isEmpty()) {
            rv.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new ArticleAdapter(articles, article -> {
                Intent intent = new Intent(this, ArticleDetailActivity.class);
                intent.putExtra("article_id", article.id);
                startActivity(intent);
            }, dm, currentUser));
        }
    }
}
