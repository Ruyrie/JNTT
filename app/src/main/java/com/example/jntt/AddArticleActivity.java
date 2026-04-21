package com.example.jntt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

public class AddArticleActivity extends AppCompatActivity {

    private ImageView ivCover, ivContentImage;
    private LinearLayout llCoverHint;
    private TextView tvRemoveContentImg;
    private EditText etTitle, etContent;

    private Uri coverUri;
    private Uri contentImageUri;

    private final ActivityResultLauncher<String> pickCover =
        registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) return;
            coverUri = uri;
            getContentResolver().takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ivCover.setImageURI(uri);
            llCoverHint.setVisibility(View.GONE);
        });

    private final ActivityResultLauncher<String> pickContentImage =
        registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) return;
            contentImageUri = uri;
            getContentResolver().takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ivContentImage.setImageURI(uri);
            ivContentImage.setVisibility(View.VISIBLE);
            tvRemoveContentImg.setVisibility(View.VISIBLE);
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        etTitle          = findViewById(R.id.etArticleTitle);
        etContent        = findViewById(R.id.etArticleContent);
        ivCover          = findViewById(R.id.ivCover);
        llCoverHint      = findViewById(R.id.llCoverHint);
        ivContentImage   = findViewById(R.id.ivContentImage);
        tvRemoveContentImg = findViewById(R.id.tvRemoveContentImg);
        FrameLayout flCover        = findViewById(R.id.flCoverPicker);
        FrameLayout flContentImage = findViewById(R.id.flContentImagePicker);
        TextView tvBack            = findViewById(R.id.tvBack);

        flCover.setOnClickListener(v -> pickCover.launch("image/*"));
        flContentImage.setOnClickListener(v -> pickContentImage.launch("image/*"));
        tvBack.setOnClickListener(v -> finish());

        tvRemoveContentImg.setOnClickListener(v -> {
            contentImageUri = null;
            ivContentImage.setVisibility(View.GONE);
            tvRemoveContentImg.setVisibility(View.GONE);
        });

        findViewById(R.id.btnSubmitArticle).setOnClickListener(v -> {
            String title   = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            String cover = coverUri != null ? coverUri.toString() : null;
            DataManager.getInstance(this).addArticle(title, content, cover);
            Toast.makeText(this, "文章发布成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
