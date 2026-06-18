package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.UserAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.User;
import java.util.List;

/** 账号管理界面：列出所有账号，支持短按修改密码、长按删除、添加账号 */
/**
 * 项目职责：账号管理页，负责账号列表、添加账号、修改密码和删除账号确认。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class AccountManagerActivity extends AppCompatActivity {

    private DataManager dm;
    private List<User> users;
    private UserAdapter adapter;

    /**
     * 项目职责：初始化账号管理页，负责账号列表、添加账号、修改密码和删除账号确认，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局；绑定布局控件；连接 RecyclerView 与 Adapter。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        dm = DataManager.getInstance(this);

        RecyclerView rv = findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        users = dm.getUsers();
        adapter = new UserAdapter(users, new UserAdapter.OnItemListener() {
            /**
             * 项目职责：账号管理页，负责账号列表、添加账号、修改密码和删除账号确认。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onShortClick(User user) {
                // 修改密码
                Intent intent = new Intent(AccountManagerActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", user.username);
                startActivity(intent);
            }

            /**
             * 项目职责：账号管理页，负责账号列表、添加账号、修改密码和删除账号确认。
             * 关键调用：绑定布局控件；绑定点击事件。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onLongClick(User user) {
                // 长按删除，不能删除当前登录账号
                String current = dm.getLoggedUser();
                if (user.username.equals(current)) {
                    android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                    android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
                    android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
                    tvTitle.setText("提示");
                    tvMessage.setText("不能删除当前登录的账号");

                    androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(
                            AccountManagerActivity.this)
                            .setView(view)
                            .create();
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    view.findViewById(R.id.btnDialogCancel).setVisibility(android.view.View.GONE);
                    android.widget.TextView btnConfirm = view.findViewById(R.id.btnDialogConfirm);
                    btnConfirm.setText("确定");
                    btnConfirm.setOnClickListener(btn -> dialog.dismiss());
                    dialog.show();
                    return;
                }

                android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
                android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
                tvTitle.setText("移除记录");
                tvMessage.setText("确定从本机登录历史中移除账号 \"" + user.username + "\" 吗？（账号本身不会被注销）");

                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(
                        AccountManagerActivity.this)
                        .setView(view)
                        .create();
                if (dialog.getWindow() != null)
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                android.widget.TextView btnConfirm = view.findViewById(R.id.btnDialogConfirm);
                btnConfirm.setText("移除");
                btnConfirm.setBackgroundResource(R.drawable.bg_auth_button);

                view.findViewById(R.id.btnDialogCancel).setOnClickListener(btn -> dialog.dismiss());
                btnConfirm.setOnClickListener(btn -> {
                    dialog.dismiss();
                    dm.hideUserFromHistory(user.username);
                    refreshList();
                });
                dialog.show();
            }
        });
        rv.setAdapter(adapter);

        // 添加账号按钮（跳转到注册页，添加模式）
        findViewById(R.id.btnAddAccount).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("add_mode", true);
            startActivity(intent);
        });
    }

    /**
     * 项目职责：账号管理页，负责账号列表、添加账号、修改密码和删除账号确认回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    /**
     * 项目职责：账号管理页，负责账号列表、添加账号、修改密码和删除账号确认。
     * 关键调用：刷新列表。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void refreshList() {
        users.clear();
        users.addAll(dm.getUsers());
        adapter.notifyDataSetChanged();
    }
}
