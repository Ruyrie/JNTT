package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.ArticleAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Article;
import java.util.ArrayList;
import java.util.List;

/** iOS 风格全屏搜索界面 */
/**
 * 项目职责：搜索页，负责文章关键词检索和结果列表展示。
 * 技术说明：显示或隐藏软键盘；绑定布局控件；绑定点击事件；刷新列表。
 * 配合代码：配合 activity_search.xml、ArticleAdapter 和 DataManager.getArticles 使用。
 */
public class SearchActivity extends AppCompatActivity {

    private List<Article>  allArticles;
    private List<Article>  results;
    private ArticleAdapter searchAdapter;

    private RecyclerView  rvResults;
    private LinearLayout  llHome, llNoResults;
    private TextView      tvNoResultsHint;

    /**
     * 项目职责：初始化搜索页，负责文章关键词检索和结果列表展示，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；连接 RecyclerView 与 Adapter；设置列表排列方式；读写本地业务数据。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        DataManager dm          = DataManager.getInstance(this);
        String      currentUser = dm.getLoggedUser();
        allArticles = dm.getArticles();
        results     = new ArrayList<>();

        // Views
        EditText    etQuery   = findViewById(R.id.etSearchQuery);
        TextView    tvCancel  = findViewById(R.id.tvSearchCancel);
        TextView    tvClear   = findViewById(R.id.tvClearSearch);
        rvResults             = findViewById(R.id.rvSearchResults);
        llHome                = findViewById(R.id.llSearchHome);
        llNoResults           = findViewById(R.id.llNoResults);
        tvNoResultsHint       = findViewById(R.id.tvNoResultsHint);

        // RecyclerView
        searchAdapter = new ArticleAdapter(results, article -> {
            Intent i = new Intent(this, ArticleDetailActivity.class);
            i.putExtra("article_id", article.id);
            startActivity(i);
        }, dm, currentUser);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(searchAdapter);

        // Cancel → dismiss
        tvCancel.setOnClickListener(v -> finish());

        // Clear ✕ button
        tvClear.setOnClickListener(v -> etQuery.setText(""));

        // Live search
        etQuery.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                String q = s.toString().trim();
                tvClear.setVisibility(q.isEmpty() ? View.GONE : View.VISIBLE);
                performSearch(q);
            }
        });

        etQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(etQuery);
                return true;
            }
            return false;
        });

        // Hot-topic tag clicks
        wireTag(R.id.tagRice,    "水稻",   etQuery);
        wireTag(R.id.tagSmart,   "智慧农业", etQuery);
        wireTag(R.id.tagRural,   "乡村振兴", etQuery);
        wireTag(R.id.tagEco,     "农业经济", etQuery);
        wireTag(R.id.tagTech,    "科技节",  etQuery);
        wireTag(R.id.tagOrganic, "有机",   etQuery);

        // Auto-show keyboard
        etQuery.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(etQuery, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 项目职责：给搜索页热门标签绑定点击事件，点击后把关键词填入搜索框。
     * 关键调用：绑定布局控件；绑定点击事件。
     * 配合代码：配合 activity_search.xml、ArticleAdapter 和 DataManager.getArticles 使用。
     */
    private void wireTag(int viewId, String query, EditText et) {
        View v = findViewById(viewId);
        if (v != null) v.setOnClickListener(x -> {
            et.setText(query);
            et.setSelection(query.length());
        });
    }

    /**
     * 项目职责：搜索页，负责文章关键词检索和结果列表展示。
     * 关键调用：刷新列表。
     * 配合代码：配合 activity_search.xml、ArticleAdapter 和 DataManager.getArticles 使用。
     */
    private void performSearch(String query) {
        if (query.isEmpty()) {
            rvResults.setVisibility(View.GONE);
            llNoResults.setVisibility(View.GONE);
            llHome.setVisibility(View.VISIBLE);
            return;
        }

        llHome.setVisibility(View.GONE);
        results.clear();
        for (Article a : allArticles) {
            if (a.title.contains(query) || a.content.contains(query) || a.author.contains(query)) {
                results.add(a);
            }
        }
        searchAdapter.notifyDataSetChanged();

        if (results.isEmpty()) {
            rvResults.setVisibility(View.GONE);
            tvNoResultsHint.setText("未找到\"" + query + "\"的相关内容");
            llNoResults.setVisibility(View.VISIBLE);
        } else {
            rvResults.setVisibility(View.VISIBLE);
            llNoResults.setVisibility(View.GONE);
        }
    }

    /**
     * 项目职责：搜索提交后收起软键盘，避免遮挡结果列表。
     * 关键调用：显示或隐藏软键盘。
     * 配合代码：配合 activity_search.xml、ArticleAdapter 和 DataManager.getArticles 使用。
     */
    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
