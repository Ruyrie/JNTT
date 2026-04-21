package com.example.jntt;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.jntt.fragment.HeadlineFragment;
import com.example.jntt.fragment.MallFragment;
import com.example.jntt.fragment.MineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** 主界面，包含头条、商城、我的三个 Tab */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottomNav);

        // 默认显示头条
        if (savedInstanceState == null) {
            switchFragment(new HeadlineFragment());
        }

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_headline) {
                switchFragment(new HeadlineFragment());
            } else if (id == R.id.nav_mall) {
                switchFragment(new MallFragment());
            } else if (id == R.id.nav_mine) {
                switchFragment(new MineFragment());
            }
            return true;
        });
    }

    private void switchFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, f)
            .commit();
    }
}
