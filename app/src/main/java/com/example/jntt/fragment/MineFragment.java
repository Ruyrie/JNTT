package com.example.jntt.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.jntt.CartActivity;
import com.example.jntt.MyArticlesActivity;
import com.example.jntt.MyOrdersActivity;
import com.example.jntt.ProfileEditActivity;
import com.example.jntt.R;
import com.example.jntt.SettingsActivity;
import com.example.jntt.data.DataManager;

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
        bindViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) bindViews(getView());
    }

    private void bindViews(View view) {
        DataManager dm = DataManager.getInstance(requireContext());
        String username = dm.getLoggedUser();

        ((TextView) view.findViewById(R.id.tvMineUsername)).setText(username);
        String nick = dm.getNickname(username);
        ((TextView) view.findViewById(R.id.tvMineNickname)).setText(
                nick.equals(username) ? "点击编辑资料" : nick);

        // 头像
        ImageView ivAvatar      = view.findViewById(R.id.ivMineAvatar);
        TextView tvInitial       = view.findViewById(R.id.tvAvatarInitial);
        String avatarUri         = dm.getAvatarUri(username);
        if (avatarUri != null) {
            try {
                ivAvatar.setImageURI(Uri.parse(avatarUri));
                ivAvatar.setVisibility(View.VISIBLE);
                tvInitial.setVisibility(View.GONE);
            } catch (Exception e) {
                ivAvatar.setVisibility(View.GONE);
                tvInitial.setVisibility(View.VISIBLE);
            }
        } else {
            // 首字母
            String initial = username.length() > 0
                    ? String.valueOf(username.charAt(0)).toUpperCase() : "我";
            tvInitial.setText(initial);
            ivAvatar.setVisibility(View.GONE);
            tvInitial.setVisibility(View.VISIBLE);
        }

        // 点击头像区 → 编辑资料
        view.findViewById(R.id.layoutProfile).setOnClickListener(v ->
                startActivity(new Intent(getContext(), ProfileEditActivity.class)));

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
