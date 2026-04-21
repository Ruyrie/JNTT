package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

/** 设置界面：账号管理、退出登录、管理员模式 */
public class SettingsActivity extends AppCompatActivity {

    private DataManager dm;
    private LinearLayout layoutAdminOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("设置");

        dm = DataManager.getInstance(this);

        // 账号管理
        findViewById(R.id.tvAccountManager).setOnClickListener(v ->
            startActivity(new Intent(this, AccountManagerActivity.class)));

        // 退出登录
        findViewById(R.id.tvLogout).setOnClickListener(v -> confirmLogout());

        // 管理员模式开关
        Switch swAdmin = findViewById(R.id.swAdminMode);
        layoutAdminOptions = findViewById(R.id.layoutAdminOptions);
        swAdmin.setChecked(dm.isAdminMode());
        updateAdminVisibility(dm.isAdminMode());

        swAdmin.setOnCheckedChangeListener((btn, checked) -> {
            dm.setAdminMode(checked);
            updateAdminVisibility(checked);
        });

        // 添加文章
        findViewById(R.id.tvAddArticle).setOnClickListener(v ->
            startActivity(new Intent(this, AddArticleActivity.class)));

        // 添加商品
        findViewById(R.id.tvAddProduct).setOnClickListener(v ->
            startActivity(new Intent(this, AddProductActivity.class)));
    }

    private void updateAdminVisibility(boolean enabled) {
        layoutAdminOptions.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？")
            .setPositiveButton("确定", (d, w) -> {
                dm.logout();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })
            .setNegativeButton("取消", null)
            .show();
    }
}
