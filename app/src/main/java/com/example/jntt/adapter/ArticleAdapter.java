package com.example.jntt.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.Article;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    private final List<Article> data;
    private final OnItemClickListener listener;

    // Simple color list for placeholder thumbnails
    private static final int[] THUMB_COLORS = {
        0xFFE8F5E9, 0xFFFFF3E0, 0xFFE3F2FD, 0xFFFCE4EC,
        0xFFF3E5F5, 0xFFE0F2F1, 0xFFFFF8E1
    };

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
        String preview = a.content.length() > 80 ? a.content.substring(0, 80) + "…" : a.content;
        holder.tvContent.setText(preview);
        holder.tvAuthor.setText(a.author);
        holder.tvTime.setText(a.time);
        holder.tvReadCount.setText("阅读 " + a.readCount + "次");
        if (a.coverUri != null) {
            holder.ivThumb.setImageURI(Uri.parse(a.coverUri));
        } else {
            switch (a.id) {
                case 1: holder.ivThumb.setImageResource(R.mipmap.text1); break;
                case 2: holder.ivThumb.setImageResource(R.mipmap.text2); break;
                case 3: holder.ivThumb.setImageResource(R.mipmap.text3); break;
                case 4: holder.ivThumb.setImageResource(R.mipmap.text4); break;
                default:
                    holder.ivThumb.setBackgroundColor(THUMB_COLORS[position % THUMB_COLORS.length]);
                    holder.ivThumb.setImageResource(R.drawable.ic_product_placeholder);
            }
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(a));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvAuthor, tvTime, tvReadCount;
        ImageView ivThumb;
        VH(View v) {
            super(v);
            tvTitle     = v.findViewById(R.id.tvArticleTitle);
            tvContent   = v.findViewById(R.id.tvArticleContent);
            tvAuthor    = v.findViewById(R.id.tvArticleAuthor);
            tvTime      = v.findViewById(R.id.tvArticleTime);
            tvReadCount = v.findViewById(R.id.tvArticleReadCount);
            ivThumb     = v.findViewById(R.id.ivArticleThumb);
        }
    }
}
