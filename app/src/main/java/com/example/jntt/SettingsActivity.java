package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

public class SettingsActivity extends AppCompatActivity {

    private DataManager dm;
    private LinearLayout layoutAdminOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dm = DataManager.getInstance(this);

        findViewById(R.id.tvAccountManager).setOnClickListener(v ->
            startActivity(new Intent(this, AccountManagerActivity.class)));

        findViewById(R.id.tvLogout).setOnClickListener(v -> confirmLogout());

        Switch swAdmin = findViewById(R.id.swAdminMode);
        layoutAdminOptions = findViewById(R.id.layoutAdminOptions);
        swAdmin.setChecked(dm.isAdminMode());
        updateAdminVisibility(dm.isAdminMode());

        swAdmin.setOnCheckedChangeListener((btn, checked) -> {
            dm.setAdminMode(checked);
            updateAdminVisibility(checked);
        });

        findViewById(R.id.tvAddArticle).setOnClickListener(v ->
            startActivity(new Intent(this, AddArticleActivity.class)));

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
