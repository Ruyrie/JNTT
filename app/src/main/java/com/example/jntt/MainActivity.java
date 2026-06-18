package com.example.jntt;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.jntt.fragment.HeadlineFragment;
import com.example.jntt.fragment.MallFragment;
import com.example.jntt.fragment.MineFragment;
import com.example.jntt.view.BlurBehindView;

/** 主界面：iOS 液态玻璃风格底部导航 */
public class MainActivity extends AppCompatActivity {

    private static final int ACTIVE_COLOR = 0xFF2F80ED;
    private static final int INACTIVE_COLOR = 0xFF1F1F1F;
    private static final long EXIT_INTERVAL_MS = 2000L;

    private View glassPill;
    private LinearLayout tabBar;
    private LinearLayout[] tabs;
    private ImageView[] icons;
    private TextView[] labels;
    private int currentIndex = 0;
    private ValueAnimator pillAnimator;
    private long lastBackPressedAt = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View fragmentContainer = findViewById(R.id.fragmentContainer);
        BlurBehindView navBlur = findViewById(R.id.navBlur);
        if (navBlur != null) {
            navBlur.setSourceView(fragmentContainer);
            navBlur.setBlurRadius(28f);
        }

        glassPill = findViewById(R.id.glassPill);
        tabBar = findViewById(R.id.glassTabBar);
        tabs = new LinearLayout[] {
                findViewById(R.id.tabHeadline),
                findViewById(R.id.tabMall),
                findViewById(R.id.tabMine)
        };
        icons = new ImageView[] {
                findViewById(R.id.iconHeadline),
                findViewById(R.id.iconMall),
                findViewById(R.id.iconMine)
        };
        labels = new TextView[] {
                findViewById(R.id.labelHeadline),
                findViewById(R.id.labelMall),
                findViewById(R.id.labelMine)
        };

        for (int i = 0; i < tabs.length; i++) {
            final int idx = i;
            tabs[i].setOnClickListener(v -> selectTab(idx, true));
        }

        // 等布局完成后，把药丸定位到第一个 tab
        tabBar.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tabBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int tabWidth = tabs[0].getWidth();
                        float density = getResources().getDisplayMetrics().density;
                        int dp24 = (int) (24 * density);
                        float dp12 = 12 * density;

                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) glassPill.getLayoutParams();
                        lp.width = tabWidth - dp24;
                        glassPill.setLayoutParams(lp);
                        glassPill.setTranslationX(tabs[currentIndex].getLeft() + dp12);
                    }
                });

        if (savedInstanceState == null) {
            switchFragment(new HeadlineFragment(), true);
        }

        // 注册返回回调：兼容 Android 13+ 预测式返回手势（targetSdk>=35 默认开启），
        // 旧式 onBackPressed() 重写在此情况下不再被可靠回调，会导致直接返回桌面。
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long now = System.currentTimeMillis();
                if (now - lastBackPressedAt <= EXIT_INTERVAL_MS) {
                    moveTaskToBack(true);
                    return;
                }
                lastBackPressedAt = now;
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectTab(int idx, boolean animate) {
        if (idx == currentIndex)
            return;

        if (pillAnimator != null && pillAnimator.isRunning()) {
            pillAnimator.cancel();
        }

        float density = getResources().getDisplayMetrics().density;
        float dp12 = 12 * density;

        // 滑动液态药丸
        float startX = glassPill.getTranslationX();
        float targetX = tabs[idx].getLeft() + dp12;
        // 强行使用当前 tab 的实际标准宽度，不依赖 getWidth，防止连点时变长变扁
        float pillWidth = tabs[idx].getWidth() - 24 * density;

        if (animate) {
            // 根据跨越的标签数量动态调整动画时长，确保不同距离的滑动速度视觉上一致
            long duration = 350 + Math.abs(idx - currentIndex) * 80L;
            pillAnimator = ValueAnimator.ofFloat(0f, 1f);
            pillAnimator.setDuration(duration);
            pillAnimator.addUpdateListener(a -> {
                float t = (float) a.getAnimatedValue();

                // 头部(冲锋端)：Overshoot 过冲效果，超过再弹回
                float tension = 1.5f;
                float tHead = (t - 1.0f);
                tHead = tHead * tHead * ((tension + 1) * tHead + tension) + 1.0f;

                // 尾部(拖拽端)：平滑的 EaseInOut 曲线 (不再死死粘连，而是整体平滑跟进)
                // 这样在滑动时，前后端速度差会产生自然的“变宽再恢复”的水滴感
                float tTail = t * t * (3.0f - 2.0f * t);

                float currentLeft, currentRight;
                if (targetX > startX) { // 向右滑动
                    // 头部是右边，冲出去
                    currentRight = (startX + pillWidth) + (targetX - startX) * tHead;
                    // 尾部是左边，慢慢跟上
                    currentLeft = startX + (targetX - startX) * tTail;
                } else { // 向左滑动
                    // 头部是左边，向左冲出去 (注意 targetX - startX 是负数)
                    currentLeft = startX + (targetX - startX) * tHead;
                    // 尾部是右边，慢慢跟上
                    currentRight = (startX + pillWidth) + (targetX - startX) * tTail;
                }

                // 纯横向的水滴拉伸和果冻反弹效果
                glassPill.setTranslationX(currentLeft);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) glassPill.getLayoutParams();
                lp.width = Math.max(1, (int) (currentRight - currentLeft));
                glassPill.setLayoutParams(lp);
            });
            pillAnimator.start();

            // 图标颜色渐变
            animateIconColor(icons[currentIndex], ACTIVE_COLOR, INACTIVE_COLOR);
            animateIconColor(icons[idx], INACTIVE_COLOR, ACTIVE_COLOR);
            labels[currentIndex].setTextColor(INACTIVE_COLOR);
            labels[currentIndex].setTypeface(null, android.graphics.Typeface.NORMAL);
            labels[idx].setTextColor(ACTIVE_COLOR);
            labels[idx].setTypeface(null, android.graphics.Typeface.BOLD);

        } else {
            glassPill.setTranslationX(targetX);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) glassPill.getLayoutParams();
            lp.width = (int) pillWidth;
            glassPill.setLayoutParams(lp);
        }

        currentIndex = idx;
        Fragment f;
        if (idx == 0)
            f = new HeadlineFragment();
        else if (idx == 1)
            f = new MallFragment();
        else
            f = new MineFragment();
        switchFragment(f, false);
    }

    private void animateIconColor(ImageView iv, int from, int to) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(280);
        anim.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            int c = ColorUtils.blendARGB(from, to, t);
            iv.setColorFilter(c);
        });
        anim.start();
    }

    private void switchFragment(Fragment f, boolean first) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragmentContainer, f).commit();
    }

}
