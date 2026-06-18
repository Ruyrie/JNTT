package com.example.jntt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.ImagePickerAdapter;
import com.example.jntt.data.DataManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目职责：商品评价发布页，负责评价内容和图片提交。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class AddProductCommentActivity extends AppCompatActivity {

    private int productId;
    private List<Uri> imageUris = new ArrayList<>();
    private ImagePickerAdapter adapter;
    private Uri currentCameraUri;

    private final ActivityResultLauncher<String> pickImages = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(), uris -> {
                if (uris != null && !uris.isEmpty()) {
                    for (Uri uri : uris) {
                        try {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        if (imageUris.size() < 9) {
                            imageUris.add(uri);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentCameraUri != null) {
                    if (imageUris.size() < 9) {
                        imageUris.add(currentCameraUri);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

    /**
     * 项目职责：初始化商品评价发布页，负责评价内容和图片提交，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；刷新列表；连接 RecyclerView 与 Adapter；设置列表排列方式。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_comment);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        productId = getIntent().getIntExtra("product_id", -1);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        EditText etContent = findViewById(R.id.etContent);
        TextView btnSubmit = findViewById(R.id.btnSubmitComment);
        RecyclerView rvCommentImages = findViewById(R.id.rvCommentImages);

        rvCommentImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagePickerAdapter(imageUris, 9, new ImagePickerAdapter.OnImagePickerClickListener() {
            /**
             * 项目职责：把图片选择器中的添加图片点击回调给宿主页面打开拍照/相册。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onAddClick() {
                showImagePickerDialog();
            }

            /**
             * 项目职责：把图片或条目删除点击回调给宿主页面移除数据并刷新列表。
             * 关键调用：刷新列表。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onDeleteClick(int position) {
                imageUris.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        rvCommentImages.setAdapter(adapter);

        DataManager dm = DataManager.getInstance(this);
        String username = dm.getLoggedUser();

        btnSubmit.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            if (content.isEmpty() && imageUris.isEmpty()) {
                Toast.makeText(this, "评价内容和图片不能同时为空", Toast.LENGTH_SHORT).show();
                return;
            }
            String images = null;
            if (!imageUris.isEmpty()) {
                images = imageUris.stream().map(Uri::toString).collect(Collectors.joining(","));
            }
            dm.addProductComment(productId, username, content, images);
            Toast.makeText(this, "评价发布成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    /**
     * 项目职责：商品评价发布页，负责评价内容和图片提交。
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
    private void showImagePickerDialog() {
        com.example.jntt.utils.ImageUtils.showImagePickerDialog(this, "添加图片",
                new com.example.jntt.utils.ImageUtils.OnImagePickerListener() {
                    /**
                     * 项目职责：把拍照选项回调给宿主页面启动相机。
                     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
                     */
                    @Override
                    public void onTakePhoto() {
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
                        pickImages.launch("image/*");
                    }
                });
    }
}
