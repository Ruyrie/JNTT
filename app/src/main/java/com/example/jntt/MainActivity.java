package com.example.jntt;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    private View glassPill;
    private LinearLayout tabBar;
    private LinearLayout[] tabs;
    private ImageView[] icons;
    private TextView[] labels;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View fragmentContainer = findViewById(R.id.fragmentContainer);
        BlurBehindView navBlur = findViewById(R.id.navBlur);
        BlurBehindView pillBlur = findViewById(R.id.pillBlur);
        navBlur.setSourceView(fragmentContainer);
        navBlur.setBlurRadius(28f);
        pillBlur.setSourceView(fragmentContainer);
        pillBlur.setBlurRadius(18f);

        glassPill = findViewById(R.id.glassPill);
        tabBar = findViewById(R.id.glassTabBar);
        tabs = new LinearLayout[]{
                findViewById(R.id.tabHeadline),
                findViewById(R.id.tabMall),
                findViewById(R.id.tabMine)
        };
        icons = new ImageView[]{
                findViewById(R.id.iconHeadline),
                findViewById(R.id.iconMall),
                findViewById(R.id.iconMine)
        };
        labels = new TextView[]{
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
                        FrameLayout.LayoutParams lp =
                                (FrameLayout.LayoutParams) glassPill.getLayoutParams();
                        lp.width = tabWidth - 12;
                        glassPill.setLayoutParams(lp);
                        glassPill.setTranslationX(tabs[currentIndex].getLeft() + 6f);
                    }
                });

        if (savedInstanceState == null) {
            switchFragment(new HeadlineFragment(), true);
        }
    }

    private void selectTab(int idx, boolean animate) {
        if (idx == currentIndex) return;

        // 滑动液态药丸
        float targetX = tabs[idx].getLeft() + 6f;
        if (animate) {
            ValueAnimator anim = ValueAnimator.ofFloat(glassPill.getTranslationX(), targetX);
            anim.setDuration(420);
            anim.setInterpolator(new PathInterpolator(0.34f, 1.56f, 0.64f, 1f));
            anim.addUpdateListener(a -> glassPill.setTranslationX((float) a.getAnimatedValue()));
            anim.start();

            // 图标颜色渐变 + 轻微缩放（液态形变感）
            animateIconColor(icons[currentIndex], ACTIVE_COLOR, INACTIVE_COLOR);
            animateIconColor(icons[idx], INACTIVE_COLOR, ACTIVE_COLOR);
            labels[currentIndex].setTextColor(INACTIVE_COLOR);
            labels[currentIndex].setTypeface(null, android.graphics.Typeface.NORMAL);
            labels[idx].setTextColor(ACTIVE_COLOR);
            labels[idx].setTypeface(null, android.graphics.Typeface.BOLD);


        } else {
            glassPill.setTranslationX(targetX);
        }

        currentIndex = idx;
        Fragment f;
        if (idx == 0) f = new HeadlineFragment();
        else if (idx == 1) f = new MallFragment();
        else f = new MineFragment();
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
        if (!first) {
            tx.setCustomAnimations(R.anim.ios_fade_scale_in, R.anim.ios_fade_out);
        }
        tx.replace(R.id.fragmentContainer, f).commit();
    }
}
