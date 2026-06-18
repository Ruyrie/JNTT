package com.example.jntt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

/** 添加商品界面（仅管理员可见） */
/**
 * 项目职责：管理员添加商品页，负责商品名称、介绍、价格和图片录入。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class AddProductActivity extends AppCompatActivity {

    private List<Uri> coverUris = new ArrayList<>();
    private ImagePickerAdapter adapter;
    private RecyclerView rvProductImages;

    private Uri currentCameraUri;

    private final ActivityResultLauncher<String> pickCover = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(), uris -> {
                if (uris != null && !uris.isEmpty()) {
                    for (Uri uri : uris) {
                        try {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        if (coverUris.size() < 9) {
                            coverUris.add(uri);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentCameraUri != null) {
                    if (coverUris.size() < 9) {
                        coverUris.add(currentCameraUri);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

    /**
     * 项目职责：初始化管理员添加商品页，负责商品名称、介绍、价格和图片录入，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；刷新列表；连接 RecyclerView 与 Adapter；设置列表排列方式。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        View tvBack = findViewById(R.id.tvBack);
        EditText etName = findViewById(R.id.etProductName);
        EditText etDesc = findViewById(R.id.etProductDesc);
        EditText etPrice = findViewById(R.id.etProductPrice);
        TextView btnSubmit = findViewById(R.id.btnSubmitProduct);

        rvProductImages = findViewById(R.id.rvProductImages);
        rvProductImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagePickerAdapter(coverUris, 9, new ImagePickerAdapter.OnImagePickerClickListener() {
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
                coverUris.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        rvProductImages.setAdapter(adapter);

        tvBack.setOnClickListener(v -> finish());

        attachThousandsFormatter(etPrice);

        DataManager dm = DataManager.getInstance(this);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim().replace(",", "");
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
            String cover = null;
            if (!coverUris.isEmpty()) {
                cover = coverUris.stream().map(Uri::toString).collect(Collectors.joining(","));
            }
            dm.addProduct(name, desc, price, cover);
            Toast.makeText(this, "商品添加成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    /** Live-format the price field with thousands separators while keeping it parseable. */
    /**
     * 项目职责：管理员添加商品页，负责商品名称、介绍、价格和图片录入。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void attachThousandsFormatter(EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            private boolean editing = false;

            /**
             * 项目职责：管理员添加商品页，负责商品名称、介绍、价格和图片录入。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * 项目职责：管理员添加商品页，负责商品名称、介绍、价格和图片录入。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            /**
             * 项目职责：管理员添加商品页，负责商品名称、介绍、价格和图片录入。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void afterTextChanged(Editable s) {
                if (editing)
                    return;
                editing = true;
                String formatted = groupPrice(s.toString());
                s.replace(0, s.length(), formatted);
                editing = false;
            }
        });
    }

    /** Insert thousands separators into the integer part, preserve up to two decimals. */
    /**
     * 项目职责：管理员添加商品页，负责商品名称、介绍、价格和图片录入。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private String groupPrice(String input) {
        String raw = input.replace(",", "");
        int dot = raw.indexOf('.');
        String intPart = (dot >= 0 ? raw.substring(0, dot) : raw).replaceAll("[^0-9]", "");
        String decPart = dot >= 0 ? raw.substring(dot + 1).replaceAll("[^0-9]", "") : null;
        if (decPart != null && decPart.length() > 2)
            decPart = decPart.substring(0, 2);

        StringBuilder grouped = new StringBuilder();
        int count = 0;
        for (int i = intPart.length() - 1; i >= 0; i--) {
            grouped.append(intPart.charAt(i));
            if (++count % 3 == 0 && i != 0)
                grouped.append(',');
        }
        String intGrouped = grouped.reverse().toString();

        if (dot >= 0)
            return (intGrouped.isEmpty() ? "0" : intGrouped) + "." + decPart;
        return intGrouped;
    }

    /**
     * 项目职责：管理员添加商品页，负责商品名称、介绍、价格和图片录入。
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
        com.example.jntt.utils.ImageUtils.showImagePickerDialog(this, "添加商品图片",
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
                        pickCover.launch("image/*");
                    }
                });
    }
}
