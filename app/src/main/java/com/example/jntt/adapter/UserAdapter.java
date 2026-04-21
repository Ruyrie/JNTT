package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {

    public interface OnItemListener {
        void onShortClick(User user);
        void onLongClick(User user);
    }

    private final List<User> data;
    private final OnItemListener listener;

    public UserAdapter(List<User> data, OnItemListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        User u = data.get(position);
        holder.tvUsername.setText(u.username);
        // 首字母头像
        String initial = u.username.length() > 0
                ? String.valueOf(u.username.charAt(0)).toUpperCase() : "?";
        holder.tvInitial.setText(initial);
        holder.itemView.setOnClickListener(v -> listener.onShortClick(u));
        holder.itemView.setOnLongClickListener(v -> { listener.onLongClick(u); return true; });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvUsername, tvInitial;
        VH(View v) {
            super(v);
            tvUsername = v.findViewById(R.id.tvUsername);
            tvInitial  = v.findViewById(R.id.tvUserInitial);
        }
    }
}
