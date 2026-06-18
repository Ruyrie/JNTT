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
/**
 * 项目职责：登录页，负责账号密码校验和进入主界面。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private DataManager dm;

    /**
     * 项目职责：初始化登录页，负责账号密码校验和进入主界面，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；读写本地业务数据；页面跳转或传递参数。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
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
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvGoRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> doLogin());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    /**
     * 项目职责：登录页，负责账号密码校验和进入主界面。
     * 关键调用：绑定布局控件；绑定点击事件；提示用户操作结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void doLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查账号是否存在
        String targetUsername = dm.findUsernameByAccount(username);
        if (targetUsername == null || targetUsername.isEmpty()) {
            // 未注册账号 - 使用自定义美化弹窗
            android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
            android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
            android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
            tvTitle.setText("账号未注册");
            tvMessage.setText("该账号 (" + username + ") 尚未注册\n是否立即前往注册？");

            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            android.widget.TextView btnConfirm = view.findViewById(R.id.btnDialogConfirm);
            btnConfirm.setText("立即注册");
            btnConfirm.setBackgroundResource(R.drawable.bg_auth_button);

            view.findViewById(R.id.btnDialogCancel).setOnClickListener(btn -> dialog.dismiss());
            btnConfirm.setOnClickListener(btn -> {
                dialog.dismiss();
                startActivity(new Intent(this, RegisterActivity.class));
            });
            dialog.show();
            return;
        }

        User user = dm.login(username, password);
        if (user == null) {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
        } else {
            dm.setLoggedUser(user.username);
            goMain();
        }
    }

    /**
     * 项目职责：登录页，负责账号密码校验和进入主界面。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
