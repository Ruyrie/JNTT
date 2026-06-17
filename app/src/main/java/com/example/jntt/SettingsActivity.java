package com.example.jntt;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SwitchCompat;
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

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        dm = DataManager.getInstance(this);

        findViewById(R.id.tvAccountManager)
                .setOnClickListener(v -> startActivity(new Intent(this, AccountManagerActivity.class)));

        findViewById(R.id.tvLogout).setOnClickListener(v -> confirmLogout());

        SwitchCompat swAdmin = findViewById(R.id.swAdminMode);
        layoutAdminOptions = findViewById(R.id.layoutAdminOptions);

        // Green when ON, gray when OFF
        int[][] states = { new int[]{ android.R.attr.state_checked }, new int[]{ -android.R.attr.state_checked } };
        int[] trackColors = { 0xFF008000, 0xFFE5E5EA };
        swAdmin.setTrackTintList(new ColorStateList(states, trackColors));

        swAdmin.setChecked(dm.isAdminMode());
        updateAdminVisibility(dm.isAdminMode());

        swAdmin.setOnCheckedChangeListener((btn, checked) -> {
            dm.setAdminMode(checked);
            updateAdminVisibility(checked);
        });

        findViewById(R.id.tvAddArticle)
                .setOnClickListener(v -> startActivity(new Intent(this, AddArticleActivity.class)));

        findViewById(R.id.tvAddProduct)
                .setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));

        findViewById(R.id.tvEditProduct)
                .setOnClickListener(v -> startActivity(new Intent(this, EditProductActivity.class)));

        findViewById(R.id.tvDeleteProduct)
                .setOnClickListener(v -> startActivity(new Intent(this, DeleteProductActivity.class)));
    }

    private void updateAdminVisibility(boolean enabled) {
        layoutAdminOptions.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void confirmLogout() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
        tvTitle.setText("退出登录");
        tvMessage.setText("确定要退出登录吗？");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btnDialogCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnDialogConfirm).setOnClickListener(v -> {
            dialog.dismiss();
            dm.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        dialog.show();
    }
}
