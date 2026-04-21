package com.example.jntt.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.ArticleDetailActivity;
import com.example.jntt.R;
import com.example.jntt.adapter.ArticleAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import java.util.List;

/** 头条 Fragment：显示全部文章列表 */
public class HeadlineFragment extends Fragment {

    private List<Article> articles;
    private ArticleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.rvArticles);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        DataManager dm = DataManager.getInstance(requireContext());
        articles = dm.getArticles();

        adapter = new ArticleAdapter(articles, article -> {
            Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
            intent.putExtra("article_id", article.id);
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 返回时刷新列表（管理员可能添加了文章）
        if (adapter != null) {
            articles.clear();
            articles.addAll(DataManager.getInstance(requireContext()).getArticles());
            adapter.notifyDataSetChanged();
        }
    }
}
