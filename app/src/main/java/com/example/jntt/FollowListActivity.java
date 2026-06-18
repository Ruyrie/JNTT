package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.data.DataManager;
import java.util.List;

/**
 * 粉丝列表 / 关注列表通用界面。
 * 通过 Intent extra "type" ("followers" | "following") 和 "username" 传参。
 */
public class FollowListActivity extends AppCompatActivity {

    /**
     * 项目职责：初始化初始化关注/粉丝页，负责用户关系列表展示和关注操作：加载布局、读取参数/本地数据、绑定控件和用户操作，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；连接 RecyclerView 与 Adapter；设置列表排列方式；读写本地业务数据。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        String type = getIntent().getStringExtra("type"); // "followers" | "following" | "likes"
        String username = getIntent().getStringExtra("username");

        if (username == null) {
            finish();
            return;
        }

        boolean isFollowers = "followers".equals(type);
        boolean isLikes = "likes".equals(type);

        // Toolbar title
        TextView tvTitle = findViewById(R.id.tvFollowTitle);
        if (isLikes) {
            tvTitle.setText("获赞");
        } else {
            tvTitle.setText(isFollowers ? "粉丝" : "关注");
        }
        findViewById(R.id.tvFollowBack).setOnClickListener(v -> finish());

        DataManager dm = DataManager.getInstance(this);
        String me = dm.getLoggedUser();
        List<String> users;
        if (isLikes) {
            users = dm.getUsersWhoLikedMyArticles(username);
        } else {
            users = isFollowers ? dm.getFollowers(username) : dm.getFollowing(username);
        }

        RecyclerView rv = findViewById(R.id.rvFollowList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new FollowUserAdapter(users, me, dm));
    }

    // ─── Inner adapter ───────────────────────────────────────────────────────

    /**
     * 项目职责：FollowUserAdapter 对应的项目组件。
     * 技术说明：绑定布局控件；绑定点击事件。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    static class FollowUserAdapter extends RecyclerView.Adapter<FollowUserAdapter.VH> {

        private final List<String> users;
        private final String currentUser;
        private final DataManager dm;

        FollowUserAdapter(List<String> users, String currentUser, DataManager dm) {
            this.users = users;
            this.currentUser = currentUser;
            this.dm = dm;
        }

        /**
         * 项目职责：为初始化初始化关注/粉丝页创建 RecyclerView 列表项 ViewHolder。
         * 关键调用：加载列表项 XML 布局；加载 XML 布局。
         * 配合代码：配合当前模块的布局、数据类和调用方使用。
         */
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_follow_user, parent, false);
            return new VH(v);
        }

        /**
         * 项目职责：把当前位置的数据绑定到初始化初始化关注/粉丝页的 item 布局控件上。
         * 关键调用：显示用户选择的图片 URI。
         * 配合代码：配合当前模块的布局、数据类和调用方使用。
         */
        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            String username = users.get(position);

            // Fetch user info for avatar and nickname
            String nickname = dm.getNickname(username);
            String avatarUri = dm.getAvatarUri(username);

            // Avatar initial
            String initial = (nickname != null && !nickname.isEmpty())
                    ? String.valueOf(nickname.charAt(0)).toUpperCase()
                    : (username.isEmpty() ? "U" : String.valueOf(username.charAt(0)).toUpperCase());
            h.tvAvatar.setText(initial);

            if (avatarUri != null) {
                try {
                    if (avatarUri.startsWith("data:image")) {
                        com.example.jntt.utils.ImageUtils.setAvatarFromBase64(h.ivAvatar, avatarUri);
                    } else {
                        h.ivAvatar.setImageURI(android.net.Uri.parse(avatarUri));
                    }
                    h.ivAvatar.setVisibility(View.VISIBLE);
                    h.tvAvatar.setVisibility(View.GONE);
                } catch (Exception e) {
                    h.ivAvatar.setVisibility(View.GONE);
                    h.tvAvatar.setVisibility(View.VISIBLE);
                }
            } else {
                h.ivAvatar.setVisibility(View.GONE);
                h.tvAvatar.setVisibility(View.VISIBLE);
            }

            h.tvUsername.setText(nickname != null && !nickname.isEmpty() ? nickname : username);
            h.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MyArticlesActivity.class);
                intent.putExtra("username", username);
                v.getContext().startActivity(intent);
            });

            // Hide follow button for self
            if (username.equals(currentUser)) {
                h.tvToggle.setVisibility(View.GONE);
                return;
            }

            h.tvToggle.setVisibility(View.VISIBLE);
            refreshToggle(h, username);

            h.tvToggle.setOnClickListener(v -> {
                if (dm.isFollowing(currentUser, username)) {
                    dm.unfollowUser(currentUser, username);
                } else {
                    dm.followUser(currentUser, username);
                }
                refreshToggle(h, username);
            });
        }

        /**
         * 项目职责：初始化关注/粉丝页，负责用户关系列表展示和关注操作：加载布局、读取参数/本地数据、绑定控件和用户操作。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
         */
        private void refreshToggle(VH h, String username) {
            boolean following = dm.isFollowing(currentUser, username);
            h.tvToggle.setText(following ? "已关注" : "关注");
            h.tvToggle.setTextColor(following ? 0xFF999999 : 0xFF2F80ED);
        }

        /**
         * 项目职责：返回初始化初始化关注/粉丝页当前列表需要展示的条目数量。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：配合当前模块的布局、数据类和调用方使用。
         */
        @Override
        public int getItemCount() {
            return users.size();
        }

        /**
         * 项目职责：VH 对应的项目组件。
         * 技术说明：绑定布局控件。
         * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
         */
        static class VH extends RecyclerView.ViewHolder {
            TextView tvAvatar, tvUsername, tvToggle;
            android.widget.ImageView ivAvatar;

            VH(View v) {
                super(v);
                tvAvatar = v.findViewById(R.id.tvFollowUserAvatar);
                ivAvatar = v.findViewById(R.id.ivFollowUserAvatar);
                tvUsername = v.findViewById(R.id.tvFollowUsername);
                tvToggle = v.findViewById(R.id.tvFollowToggle);
            }
        }
    }
}
