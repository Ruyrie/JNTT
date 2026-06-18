package com.example.jntt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

/** 修改密码界面 */
/**
 * 项目职责：修改密码页，负责旧密码校验和新密码保存。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class ChangePasswordActivity extends AppCompatActivity {

    /**
     * 项目职责：初始化修改密码页，负责旧密码校验和新密码保存，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；读写本地业务数据；页面跳转或传递参数；提示用户操作结果。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        String username = getIntent().getStringExtra("username");
        DataManager dm = DataManager.getInstance(this);

        ((TextView) findViewById(R.id.tvChangeUsername)).setText("账号：" + username);

        EditText etNew = findViewById(R.id.etNewPassword);
        EditText etConfirm = findViewById(R.id.etConfirmPassword);
        Button btnSave = findViewById(R.id.btnSavePassword);

        btnSave.setOnClickListener(v -> {
            String newPwd = etNew.getText().toString().trim();
            String confirmPwd = etConfirm.getText().toString().trim();
            if (newPwd.isEmpty()) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPwd.length() < 6) {
                Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dm.isSameAsOldPassword(username, newPwd)) {
                Toast.makeText(this, "新密码不能和旧密码相同", Toast.LENGTH_SHORT).show();
                return;
            }
            dm.changePassword(username, newPwd);
            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
