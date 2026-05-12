package com.example.jntt;

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

/** 添加商品界面（仅管理员可见） */
public class AddProductActivity extends AppCompatActivity {

    private Uri coverUri;
    private ImageView ivCover;
    private LinearLayout llCoverHint;

    private Uri currentCameraUri;

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
                ivCover.setVisibility(View.VISIBLE);
                llCoverHint.setVisibility(View.GONE);
            });

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentCameraUri != null) {
                    coverUri = currentCameraUri;
                    ivCover.setImageURI(currentCameraUri);
                    ivCover.setVisibility(View.VISIBLE);
                    llCoverHint.setVisibility(View.GONE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        TextView tvBack = findViewById(R.id.tvBack);
        EditText etName = findViewById(R.id.etProductName);
        EditText etDesc = findViewById(R.id.etProductDesc);
        EditText etPrice = findViewById(R.id.etProductPrice);
        TextView btnSubmit = findViewById(R.id.btnSubmitProduct);

        ivCover = findViewById(R.id.ivProductCover);
        llCoverHint = findViewById(R.id.llProductCoverHint);
        FrameLayout flCover = findViewById(R.id.flProductCoverPicker);

        flCover.setOnClickListener(v -> showImagePickerDialog(pickCover));
        tvBack.setOnClickListener(v -> finish());

        DataManager dm = DataManager.getInstance(this);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "所有字段不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "价格格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            String cover = coverUri != null ? coverUri.toString() : null;
            dm.addProduct(name, desc, price, cover);
            Toast.makeText(this, "商品添加成功", Toast.LENGTH_SHORT).show();
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

    private void showImagePickerDialog(ActivityResultLauncher<String> launcher) {
        com.example.jntt.utils.ImageUtils.showImagePickerDialog(this, "添加商品图片",
                new com.example.jntt.utils.ImageUtils.OnImagePickerListener() {
                    @Override
                    public void onTakePhoto() {
                        currentCameraUri = createImageFile();
                        takePicture.launch(currentCameraUri);
                    }

                    @Override
                    public void onPickFromGallery() {
                        launcher.launch("image/*");
                    }
                });
    }
}
