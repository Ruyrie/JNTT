package com.example.jntt;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.ArticleAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import android.content.Intent;
import java.util.List;

/** 我的文章界面：复用文章列表，仅显示当前账号发布的文章 */
public class MyArticlesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        setTitle("我的文章");

        DataManager dm = DataManager.getInstance(this);
        String username = dm.getLoggedUser();
        List<Article> articles = dm.getArticlesByAuthor(username);

        RecyclerView rv = findViewById(R.id.rvArticles);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ArticleAdapter(articles, article -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra("article_id", article.id);
            startActivity(intent);
        }));
    }
}
