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

/**
 * 项目职责：首页头条 Fragment，负责文章列表、搜索入口和文章详情跳转。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class HeadlineFragment extends Fragment {

    private List<Article>  allArticles;
    private List<Article>  displayed;
    private ArticleAdapter adapter;

    /**
     * 项目职责：加载首页头条 Fragment，负责文章列表、搜索入口和文章详情跳转对应的 Fragment 布局文件。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 MainActivity、fragment_*.xml、DataManager 和 Adapter 使用。
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headline, container, false);
    }

    /**
     * 项目职责：在首页头条 Fragment，负责文章列表、搜索入口和文章详情跳转界面创建完成后绑定控件、数据列表和点击事件。
     * 关键调用：绑定布局控件；连接 RecyclerView 与 Adapter；设置列表排列方式；读写本地业务数据。
     * 配合代码：配合 MainActivity、fragment_*.xml、DataManager 和 Adapter 使用。
     */
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

    /**
     * 项目职责：首页头条 Fragment，负责文章列表、搜索入口和文章详情跳转回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：刷新列表；读写本地业务数据。
     * 配合代码：配合 MainActivity、fragment_*.xml、DataManager 和 Adapter 使用。
     */
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
