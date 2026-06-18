package com.example.jntt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.example.jntt.R;

/** FrameLayout，按指定圆角半径裁剪所有子 View。 */
/**
 * 项目职责：圆角裁剪容器，负责把内部子 View 裁剪成圆角。
 * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
 * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
 */
public class RoundedClipFrame extends FrameLayout {

    private float cornerRadius = 0f;

    /**
     * 项目职责：创建圆角裁剪容器，初始化绘制参数并接入 Android View 构造流程。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public RoundedClipFrame(Context c) { super(c); init(c, null); }
    /**
     * 项目职责：创建圆角裁剪容器，初始化绘制参数并接入 Android View 构造流程。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public RoundedClipFrame(Context c, @Nullable AttributeSet a) { super(c, a); init(c, a); }
    /**
     * 项目职责：创建圆角裁剪容器，初始化绘制参数并接入 Android View 构造流程。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public RoundedClipFrame(Context c, @Nullable AttributeSet a, int s) { super(c, a, s); init(c, a); }

    /**
     * 项目职责：初始化自定义 View 的画笔、路径、圆角或裁剪参数。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    private void init(Context c, @Nullable AttributeSet a) {
        if (a != null) {
            TypedArray ta = c.obtainStyledAttributes(a, R.styleable.RoundedClipFrame);
            cornerRadius = ta.getDimension(R.styleable.RoundedClipFrame_cornerRadius, 0f);
            ta.recycle();
        }
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            /**
             * 项目职责：圆角裁剪容器，负责把内部子 View 裁剪成圆角。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
             */
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
    }

    /**
     * 项目职责：圆角裁剪容器，负责把内部子 View 裁剪成圆角。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public void setCornerRadius(float r) {
        this.cornerRadius = r;
        invalidateOutline();
    }
}
