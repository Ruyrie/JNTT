package com.example.jntt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.Nullable;

/**
 * 实时背景模糊 View（iOS 液态玻璃效果）。
 * 持续抓取 sourceView 在本视图位置下的内容，叠加 RenderEffect 高斯模糊。
 * 需要 API 31+。
 */
public class BlurBehindView extends View {

    private View sourceView;
    private Bitmap snapshot;
    private Canvas snapshotCanvas;
    private float blurRadius = 25f;
    private boolean drawingSnapshot = false;

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = () -> {
        if (!drawingSnapshot) invalidate();
        return true;
    };

    /**
     * 项目职责：创建玻璃模糊背景 View，初始化绘制参数并接入 Android View 构造流程。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public BlurBehindView(Context c) { super(c); init(); }
    /**
     * 项目职责：创建玻璃模糊背景 View，初始化绘制参数并接入 Android View 构造流程。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public BlurBehindView(Context c, @Nullable AttributeSet a) { super(c, a); init(); }
    /**
     * 项目职责：创建玻璃模糊背景 View，初始化绘制参数并接入 Android View 构造流程。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public BlurBehindView(Context c, @Nullable AttributeSet a, int s) { super(c, a, s); init(); }

    /**
     * 项目职责：初始化自定义 View 的画笔、路径、圆角或裁剪参数。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    private void init() {
        setRenderEffect(RenderEffect.createBlurEffect(
                blurRadius, blurRadius, Shader.TileMode.CLAMP));
    }

    /**
     * 项目职责：设置模糊背景的截图来源 View，通常是主界面的 Fragment 容器。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public void setSourceView(View source) {
        this.sourceView = source;
    }

    /**
     * 项目职责：设置底部导航背景模糊半径。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    public void setBlurRadius(float radius) {
        this.blurRadius = radius;
        setRenderEffect(RenderEffect.createBlurEffect(
                radius, radius, Shader.TileMode.CLAMP));
    }

    /**
     * 项目职责：创建玻璃模糊背景 View，负责为底部导航绘制背景模糊效果需要的数据、监听器或上下文引用。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    /**
     * 项目职责：创建玻璃模糊背景 View，负责为底部导航绘制背景模糊效果需要的数据、监听器或上下文引用。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    @Override
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
        if (snapshot != null) { snapshot.recycle(); snapshot = null; }
        super.onDetachedFromWindow();
    }

    /**
     * 项目职责：在自定义 View 尺寸变化时重新计算绘制区域。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        if (w <= 0 || h <= 0) return;
        // 用半分辨率位图，模糊更便宜
        int bw = Math.max(1, w / 2);
        int bh = Math.max(1, h / 2);
        if (snapshot != null) snapshot.recycle();
        snapshot = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
        snapshotCanvas = new Canvas(snapshot);
    }

    /**
     * 项目职责：绘制自定义 View 的模糊背景、玻璃导航或圆角裁剪效果。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 XML 布局和 Android View 绘制流程使用。
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (sourceView == null || snapshot == null || drawingSnapshot) return;

        drawingSnapshot = true;
        try {
            // 计算 sourceView 在屏幕上的位置 vs. 本 View 在屏幕上的位置
            int[] selfLoc = new int[2];
            int[] srcLoc = new int[2];
            getLocationInWindow(selfLoc);
            sourceView.getLocationInWindow(srcLoc);
            float dx = srcLoc[0] - selfLoc[0];
            float dy = srcLoc[1] - selfLoc[1];

            snapshot.eraseColor(0);
            snapshotCanvas.save();
            float scale = (float) snapshot.getWidth() / getWidth();
            snapshotCanvas.scale(scale, scale);
            snapshotCanvas.translate(dx, dy);
            sourceView.draw(snapshotCanvas);
            snapshotCanvas.restore();

            canvas.save();
            canvas.scale((float) getWidth() / snapshot.getWidth(),
                         (float) getHeight() / snapshot.getHeight());
            canvas.drawBitmap(snapshot, 0, 0, null);
            canvas.restore();
        } finally {
            drawingSnapshot = false;
        }
    }
}
