package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

/** 注册界面（复用为添加账号界面） */
public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private DataManager dm;

    // 是否从账号管理进入（添加账号模式）
    private boolean isAddMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dm = DataManager.getInstance(this);
        isAddMode = getIntent().getBooleanExtra("add_mode", false);

        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        setTitle(isAddMode ? "添加账号" : "注册");

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean ok = dm.register(username, password);
        if (!ok) {
            Toast.makeText(this, "该用户名已存在", Toast.LENGTH_SHORT).show();
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
