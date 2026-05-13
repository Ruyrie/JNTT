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
import androidx.core.content.FileProvider;
import com.example.jntt.data.DataManager;
import java.io.File;

public class AddArticleActivity extends AppCompatActivity {

    private ImageView ivCover, ivContentImage;
    private LinearLayout llCoverHint;
    private TextView tvRemoveContentImg;
    private EditText etTitle, etContent;

    private Uri coverUri;
    private Uri contentImageUri;

    private Uri currentCameraUri;
    private boolean isPickingCoverForCamera = true;

    private final ActivityResultLauncher<String> pickCover = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null)
                    return;
                coverUri = uri;
                try {
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                ivCover.setImageURI(uri);
                llCoverHint.setVisibility(View.GONE);
            });

    private final ActivityResultLauncher<String> pickContentImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null)
                    return;
                contentImageUri = uri;
                try {
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                ivContentImage.setImageURI(uri);
                ivContentImage.setVisibility(View.VISIBLE);
                tvRemoveContentImg.setVisibility(View.VISIBLE);
            });

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentCameraUri != null) {
                    if (isPickingCoverForCamera) {
                        coverUri = currentCameraUri;
                        ivCover.setImageURI(currentCameraUri);
                        llCoverHint.setVisibility(View.GONE);
                    } else {
                        contentImageUri = currentCameraUri;
                        ivContentImage.setImageURI(currentCameraUri);
                        ivContentImage.setVisibility(View.VISIBLE);
                        tvRemoveContentImg.setVisibility(View.VISIBLE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        etTitle = findViewById(R.id.etArticleTitle);
        etContent = findViewById(R.id.etArticleContent);
        ivCover = findViewById(R.id.ivCover);
        llCoverHint = findViewById(R.id.llCoverHint);
        ivContentImage = findViewById(R.id.ivContentImage);
        tvRemoveContentImg = findViewById(R.id.tvRemoveContentImg);
        FrameLayout flCover = findViewById(R.id.flCoverPicker);
        FrameLayout flContentImage = findViewById(R.id.flContentImagePicker);
        TextView tvBack = findViewById(R.id.tvBack);

        flCover.setOnClickListener(v -> showImagePickerDialog(pickCover, true));
        flContentImage.setOnClickListener(v -> showImagePickerDialog(pickContentImage, false));
        tvBack.setOnClickListener(v -> finish());

        tvRemoveContentImg.setOnClickListener(v -> {
            contentImageUri = null;
            ivContentImage.setVisibility(View.GONE);
            tvRemoveContentImg.setVisibility(View.GONE);
        });

        findViewById(R.id.btnSubmitArticle).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
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

    private Uri createImageFile() {
        File imagePath = new File(getCacheDir(), "images");
        if (!imagePath.exists())
            imagePath.mkdirs();
        File newFile = new File(imagePath, "photo_" + System.currentTimeMillis() + ".jpg");
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", newFile);
    }

    private void showImagePickerDialog(ActivityResultLauncher<String> galleryLauncher, boolean isCover) {
        com.example.jntt.utils.ImageUtils.showImagePickerDialog(this, "添加图片",
                new com.example.jntt.utils.ImageUtils.OnImagePickerListener() {
                    @Override
                    public void onTakePhoto() {
                        isPickingCoverForCamera = isCover;
                        currentCameraUri = createImageFile();
                        takePicture.launch(currentCameraUri);
                    }

                    @Override
                    public void onPickFromGallery() {
                        galleryLauncher.launch("image/*");
                    }
                });
    }
}
