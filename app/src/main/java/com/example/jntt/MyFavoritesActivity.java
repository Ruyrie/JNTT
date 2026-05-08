package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.ArticleAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import java.util.List;

/** 我的收藏：展示当前用户点赞（收藏）的文章 */
public class MyFavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        findViewById(R.id.tvFavBack).setOnClickListener(v -> finish());

        DataManager dm       = DataManager.getInstance(this);
        String      username = dm.getLoggedUser();
        List<Article> articles = dm.getLikedArticles(username);

        RecyclerView rv = findViewById(R.id.rvArticles);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ArticleAdapter(articles, article -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra("article_id", article.id);
            startActivity(intent);
        }));
    }
}
