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

/**
 * 项目职责：管理员发文章页，负责标题/内容/封面/正文图片录入并保存文章。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
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

    /**
     * 项目职责：初始化管理员发文章页，负责标题/内容/封面/正文图片录入并保存文章，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；读写本地业务数据；提示用户操作结果；弹出确认/选择窗口。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
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
        View tvBack = findViewById(R.id.tvBack);

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

    /**
     * 项目职责：管理员发文章页，负责标题/内容/封面/正文图片录入并保存文章。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private Uri createImageFile() {
        File imagePath = new File(getCacheDir(), "images");
        if (!imagePath.exists())
            imagePath.mkdirs();
        File newFile = new File(imagePath, "photo_" + System.currentTimeMillis() + ".jpg");
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", newFile);
    }

    /**
     * 项目职责：展示拍照/相册底部弹窗，供文章、商品、评价、头像页面复用。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void showImagePickerDialog(ActivityResultLauncher<String> galleryLauncher, boolean isCover) {
        com.example.jntt.utils.ImageUtils.showImagePickerDialog(this, "添加图片",
                new com.example.jntt.utils.ImageUtils.OnImagePickerListener() {
                    /**
                     * 项目职责：把拍照选项回调给宿主页面启动相机。
                     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
                     */
                    @Override
                    public void onTakePhoto() {
                        isPickingCoverForCamera = isCover;
                        currentCameraUri = createImageFile();
                        takePicture.launch(currentCameraUri);
                    }

                    /**
                     * 项目职责：把相册选项回调给宿主页面启动图片选择器。
                     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
                     */
                    @Override
                    public void onPickFromGallery() {
                        galleryLauncher.launch("image/*");
                    }
                });
    }
}
