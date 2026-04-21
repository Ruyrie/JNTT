package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.User;

/** 登录界面 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private DataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dm = DataManager.getInstance(this);

        // 如果已经登录则直接进入主界面
        if (dm.getLoggedUser() != null) {
            goMain();
            return;
        }

        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin    = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> doLogin());
        tvRegister.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = dm.login(username, password);
        if (user == null) {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        } else {
            dm.setLoggedUser(user.username);
            goMain();
        }
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
