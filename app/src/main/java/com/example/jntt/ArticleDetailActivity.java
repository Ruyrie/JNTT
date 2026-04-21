package com.example.jntt;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        int articleId = getIntent().getIntExtra("article_id", -1);
        DataManager dm = DataManager.getInstance(this);

        // 阅读数 +1
        dm.incrementReadCount(articleId);

        Article target = null;
        for (Article a : dm.getArticles()) {
            if (a.id == articleId) { target = a; break; }
        }
        if (target == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvDetailTitle)).setText(target.title);
        ((TextView) findViewById(R.id.tvDetailAuthor)).setText("作者：" + target.author);
        ((TextView) findViewById(R.id.tvDetailTime)).setText(target.time + "  阅读 " + target.readCount);
        ((TextView) findViewById(R.id.tvDetailContent)).setText(target.content);
    }
}
