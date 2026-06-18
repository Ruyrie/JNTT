package com.example.jntt;

import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.yalantis.ucrop.UCropActivity;

/**
 * 项目职责：头像裁剪兼容页，负责处理 uCrop 页面边缘区域适配。
 * 技术说明：绑定布局控件。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class UCropCompatActivity extends UCropActivity {
    /**
     * 项目职责：初始化头像裁剪兼容页，负责处理 uCrop 页面边缘区域适配，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View content = getWindow().getDecorView().findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, bars.top, 0, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
