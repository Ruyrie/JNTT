package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.Article;
import java.util.List;

/** 文章列表适配器 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    private final List<Article> data;
    private final OnItemClickListener listener;

    public ArticleAdapter(List<Article> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Article a = data.get(position);
        holder.tvTitle.setText(a.title);
        // 内容预览截取前60字
        String preview = a.content.length() > 60 ? a.content.substring(0, 60) + "…" : a.content;
        holder.tvContent.setText(preview);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(a));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent;
        VH(View v) {
            super(v);
            tvTitle   = v.findViewById(R.id.tvArticleTitle);
            tvContent = v.findViewById(R.id.tvArticleContent);
        }
    }
}
