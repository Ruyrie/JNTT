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
import com.example.jntt.SearchActivity;
import com.example.jntt.adapter.ArticleAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import java.util.ArrayList;
import java.util.List;

public class HeadlineFragment extends Fragment {

    private List<Article>  allArticles;
    private List<Article>  displayed;
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
        DataManager dm = DataManager.getInstance(requireContext());
        allArticles = dm.getArticles();
        displayed   = new ArrayList<>(allArticles);

        RecyclerView rv = view.findViewById(R.id.rvArticles);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArticleAdapter(displayed, article -> {
            Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
            intent.putExtra("article_id", article.id);
            startActivity(intent);
        }, dm, dm.getLoggedUser());
        rv.setAdapter(adapter);

        // Search icon → full-screen iOS-style search
        view.findViewById(R.id.btnOpenSearch).setOnClickListener(v ->
                startActivity(new Intent(getContext(), SearchActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh after returning from ArticleDetail (like counts may have changed)
        if (adapter != null) {
            DataManager dm = DataManager.getInstance(requireContext());
            allArticles.clear();
            allArticles.addAll(dm.getArticles());
            displayed.clear();
            displayed.addAll(allArticles);
            adapter.notifyDataSetChanged();
        }
    }
}
