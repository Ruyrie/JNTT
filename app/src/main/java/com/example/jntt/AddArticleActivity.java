package com.example.jntt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

/** 添加文章界面（仅管理员可见） */
public class AddArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        setTitle("添加文章");

        EditText etTitle   = findViewById(R.id.etArticleTitle);
        EditText etContent = findViewById(R.id.etArticleContent);
        Button btnSubmit   = findViewById(R.id.btnSubmitArticle);

        DataManager dm = DataManager.getInstance(this);

        btnSubmit.setOnClickListener(v -> {
            String title   = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            dm.addArticle(title, content);
            Toast.makeText(this, "文章发布成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
