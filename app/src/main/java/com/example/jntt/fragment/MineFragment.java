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
import com.example.jntt.FollowListActivity;
import com.example.jntt.MyArticlesActivity;
import com.example.jntt.MyFavoritesActivity;
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
                if (getView() != null)
                        bindViews(getView());
        }

        private void bindViews(View view) {
                DataManager dm = DataManager.getInstance(requireContext());
                String username = dm.getLoggedUser();

                // Username + nickname + signature
                ((TextView) view.findViewById(R.id.tvMineUsername)).setText("用户名： " + username);

                String nick = dm.getNickname(username);
                ((TextView) view.findViewById(R.id.tvMineNickname)).setText(nick);

                String sig = dm.getSignature(username);
                ((TextView) view.findViewById(R.id.tvMineSignature)).setText(sig);

                // Avatar
                ImageView ivAvatar = view.findViewById(R.id.ivMineAvatar);
                TextView tvInitial = view.findViewById(R.id.tvAvatarInitial);
                String avatarUri = dm.getAvatarUri(username);
                if (avatarUri != null) {
                        try {
                                if (avatarUri.startsWith("data:image")) {
                                        com.example.jntt.utils.ImageUtils.setAvatarFromBase64(ivAvatar, avatarUri);
                                } else {
                                        ivAvatar.setImageURI(Uri.parse(avatarUri));
                                }
                                ivAvatar.setVisibility(View.VISIBLE);
                                tvInitial.setVisibility(View.GONE);
                        } catch (Exception e) {
                                ivAvatar.setVisibility(View.GONE);
                                tvInitial.setVisibility(View.VISIBLE);
                        }
                } else {
                        String initial = (!username.isEmpty())
                                        ? String.valueOf(username.charAt(0)).toUpperCase()
                                        : "我";
                        tvInitial.setText(initial);
                        ivAvatar.setVisibility(View.GONE);
                        tvInitial.setVisibility(View.VISIBLE);
                }

                // Stats: 粉丝 / 关注 / 获赞
                ((TextView) view.findViewById(R.id.tvStatFollowers))
                                .setText(String.valueOf(dm.getFollowersCount(username)));
                ((TextView) view.findViewById(R.id.tvStatFollowing))
                                .setText(String.valueOf(dm.getFollowingCount(username)));
                ((TextView) view.findViewById(R.id.tvStatLikes))
                                .setText(String.valueOf(dm.getTotalLikesReceived(username)));

                // Stat row click handlers (separate from layoutProfile)
                view.findViewById(R.id.statFollowers).setOnClickListener(v -> {
                        Intent i = new Intent(getContext(), FollowListActivity.class);
                        i.putExtra("type", "followers");
                        i.putExtra("username", username);
                        startActivity(i);
                });
                view.findViewById(R.id.statFollowing).setOnClickListener(v -> {
                        Intent i = new Intent(getContext(), FollowListActivity.class);
                        i.putExtra("type", "following");
                        i.putExtra("username", username);
                        startActivity(i);
                });
                view.findViewById(R.id.statLikes).setOnClickListener(v -> {
                        Intent i = new Intent(getContext(), FollowListActivity.class);
                        i.putExtra("type", "likes");
                        i.putExtra("username", username);
                        startActivity(i);
                });

                // Click listeners
                view.findViewById(R.id.layoutProfile).setOnClickListener(
                                v -> startActivity(new Intent(getContext(), ProfileEditActivity.class)));

                view.findViewById(R.id.tvMyArticles).setOnClickListener(
                                v -> startActivity(new Intent(getContext(), MyArticlesActivity.class)));

                view.findViewById(R.id.tvMyFavorites).setOnClickListener(
                                v -> startActivity(new Intent(getContext(), MyFavoritesActivity.class)));

                view.findViewById(R.id.tvMyOrders).setOnClickListener(
                                v -> startActivity(new Intent(getContext(), MyOrdersActivity.class)));

                view.findViewById(R.id.tvCart)
                                .setOnClickListener(v -> startActivity(new Intent(getContext(), CartActivity.class)));

                view.findViewById(R.id.tvSettings).setOnClickListener(
                                v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
        }
}
