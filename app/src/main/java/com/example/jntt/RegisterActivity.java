package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

/** 注册界面（复用为添加账号界面） */
/**
 * 项目职责：注册页，负责新账号创建。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etPhone;
    private DataManager dm;

    // 是否从账号管理进入（添加账号模式）
    private boolean isAddMode = false;

    /**
     * 项目职责：初始化注册页，负责新账号创建，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；读写本地业务数据；页面跳转或传递参数。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dm = DataManager.getInstance(this);
        isAddMode = getIntent().getBooleanExtra("add_mode", false);

        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        etPhone = findViewById(R.id.etRegPhone);
        Button btnRegister = findViewById(R.id.btnRegister);
        LinearLayout layoutGoLogin = findViewById(R.id.layoutGoLogin);
        TextView tvGoLogin = findViewById(R.id.tvGoLogin);

        setTitle(isAddMode ? "添加账号" : "注册");

        btnRegister.setOnClickListener(v -> doRegister());
        if (isAddMode) {
            layoutGoLogin.setVisibility(View.GONE);
        } else {
            tvGoLogin.setOnClickListener(v -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    /**
     * 项目职责：注册页，负责新账号创建。
     * 关键调用：提示用户操作结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void doRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!username.matches("^[a-zA-Z0-9\\-@_.]+$")) {
            Toast.makeText(this, "用户名只能包含字母、数字及-@_.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.isEmpty()) {
            if (phone.length() != 11 || !phone.matches("^1[3-9]\\d{9}$") || phone.matches("^(\\d)\\1{10}$")) {
                Toast.makeText(this, "请输入有效的11位手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dm.isPhoneBound(phone)) {
                Toast.makeText(this, "该手机号已被注册或绑定，请更换手机号", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean ok = dm.register(username, password, phone);
        if (!ok) {
            Toast.makeText(this, "该用户名已被使用，请更换用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();

        if (isAddMode) {
            // 账号管理模式，直接返回
            finish();
        } else {
            // 注册完成后自动登录并进入主界面
            dm.setLoggedUser(username);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
