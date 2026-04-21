package com.example.jntt.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.jntt.CartActivity;
import com.example.jntt.MyArticlesActivity;
import com.example.jntt.MyOrdersActivity;
import com.example.jntt.R;
import com.example.jntt.SettingsActivity;
import com.example.jntt.data.DataManager;

/** 我的 Fragment：个人信息入口汇总 */
public class MineFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DataManager dm = DataManager.getInstance(requireContext());
        String username = dm.getLoggedUser();

        // 显示用户名
        TextView tvName = view.findViewById(R.id.tvMineUsername);
        tvName.setText(username);

        view.findViewById(R.id.tvMyArticles).setOnClickListener(v ->
            startActivity(new Intent(getContext(), MyArticlesActivity.class)));

        view.findViewById(R.id.tvMyOrders).setOnClickListener(v ->
            startActivity(new Intent(getContext(), MyOrdersActivity.class)));

        view.findViewById(R.id.tvCart).setOnClickListener(v ->
            startActivity(new Intent(getContext(), CartActivity.class)));

        view.findViewById(R.id.tvSettings).setOnClickListener(v ->
            startActivity(new Intent(getContext(), SettingsActivity.class)));
    }
}
