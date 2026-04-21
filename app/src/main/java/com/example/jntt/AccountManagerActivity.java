package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.UserAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.User;
import java.util.List;

/** 账号管理界面：列出所有账号，支持短按修改密码、长按删除、添加账号 */
public class AccountManagerActivity extends AppCompatActivity {

    private DataManager dm;
    private List<User> users;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        setTitle("账号管理");

        dm = DataManager.getInstance(this);

        RecyclerView rv = findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        users = dm.getUsers();
        adapter = new UserAdapter(users, new UserAdapter.OnItemListener() {
            @Override
            public void onShortClick(User user) {
                // 修改密码
                Intent intent = new Intent(AccountManagerActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", user.username);
                startActivity(intent);
            }

            @Override
            public void onLongClick(User user) {
                // 长按删除，不能删除当前登录账号
                String current = dm.getLoggedUser();
                if (user.username.equals(current)) {
                    new AlertDialog.Builder(AccountManagerActivity.this)
                        .setMessage("不能删除当前登录的账号").setPositiveButton("确定", null).show();
                    return;
                }
                new AlertDialog.Builder(AccountManagerActivity.this)
                    .setTitle("删除账号")
                    .setMessage("确定删除账号 \"" + user.username + "\" 吗？")
                    .setPositiveButton("删除", (d, w) -> {
                        dm.deleteUser(user.username);
                        refreshList();
                    })
                    .setNegativeButton("取消", null)
                    .show();
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        users.clear();
        users.addAll(dm.getUsers());
        adapter.notifyDataSetChanged();
    }
}

