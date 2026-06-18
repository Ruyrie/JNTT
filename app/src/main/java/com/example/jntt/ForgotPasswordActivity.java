package com.example.jntt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;
import com.example.jntt.utils.CaptchaUtils;

/**
 * 项目职责：忘记密码页，负责找回密码流程入口。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etAccount, etCaptcha;
    private ImageView ivCaptcha;
    private DataManager dm;
    private String realCaptcha;

    /**
     * 项目职责：初始化忘记密码页，负责找回密码流程入口，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；读写本地业务数据。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dm = DataManager.getInstance(this);

        etAccount = findViewById(R.id.etForgotAccount);
        etCaptcha = findViewById(R.id.etForgotCaptcha);
        ivCaptcha = findViewById(R.id.ivCaptcha);
        Button btnNextStep = findViewById(R.id.btnNextStep);

        refreshCaptcha();

        ivCaptcha.setOnClickListener(v -> refreshCaptcha());

        btnNextStep.setOnClickListener(v -> doNextStep());
    }

    /**
     * 项目职责：忘记密码页，负责找回密码流程入口。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void refreshCaptcha() {
        CaptchaUtils utils = CaptchaUtils.getInstance();
        Bitmap bitmap = utils.createBitmap();
        realCaptcha = utils.getCode();
        ivCaptcha.setImageBitmap(bitmap);
    }

    /**
     * 项目职责：忘记密码页，负责找回密码流程入口。
     * 关键调用：提示用户操作结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void doNextStep() {
        String account = etAccount.getText().toString().trim();
        String inputCaptcha = etCaptcha.getText().toString().trim();

        if (account.isEmpty()) {
            Toast.makeText(this, "请输入用户名或手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputCaptcha.isEmpty()) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inputCaptcha.equalsIgnoreCase(realCaptcha)) {
            Toast.makeText(this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
            refreshCaptcha();
            etCaptcha.setText("");
            return;
        }

        String targetUsername = dm.findUsernameByAccount(account);
        if (targetUsername == null || targetUsername.isEmpty()) {
            Toast.makeText(this, "未找到该用户，请检查输入的用户名或手机号", Toast.LENGTH_SHORT).show();
            refreshCaptcha();
            return;
        }

        // 验证通过，进入下一个页面
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra("username", targetUsername);
        intent.putExtra("display_account", account);
        startActivity(intent);
        finish();
    }
}
