package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.ArticleAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import java.util.List;

/** 我的收藏：展示当前用户点赞（收藏）的文章 */
/**
 * 项目职责：我的收藏页，负责收藏文章列表、已删除占位和移除收藏。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class MyFavoritesActivity extends AppCompatActivity {

    private DataManager dm;
    private String username;
    private RecyclerView rv;
    private ArticleAdapter adapter;

    /**
     * 项目职责：初始化我的收藏页，负责收藏文章列表、已删除占位和移除收藏，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局；绑定布局控件；设置列表排列方式。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        findViewById(R.id.tvFavBack).setOnClickListener(v -> finish());

        dm = DataManager.getInstance(this);
        username = dm.getLoggedUser();

        rv = findViewById(R.id.rvArticles);
        rv.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        findViewById(R.id.tvClearInvalid).setOnClickListener(v -> {
            android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
            android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
            tvTitle.setText("清理失效文章");
            tvMessage.setText("确定要将已删除的文章移出收藏列表吗？");

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            view.findViewById(R.id.btnDialogCancel).setOnClickListener(btn -> dialog.dismiss());
            view.findViewById(R.id.btnDialogConfirm).setOnClickListener(btn -> {
                dialog.dismiss();
                int cleared = dm.clearInvalidLikedArticles(username);
                if (cleared > 0) {
                    Toast.makeText(this, "成功清理 " + cleared + " 篇失效文章", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "没有需要清理的失效文章", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        });
    }

    /**
     * 项目职责：我的收藏页，负责收藏文章列表、已删除占位和移除收藏回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * 项目职责：我的收藏页，负责收藏文章列表、已删除占位和移除收藏。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void loadData() {
        List<Article> articles = dm.getLikedArticles(username);
        adapter = new ArticleAdapter(articles, article -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra("article_id", article.id);
            startActivity(intent);
        }, dm, username);
        rv.setAdapter(adapter);
    }
}
