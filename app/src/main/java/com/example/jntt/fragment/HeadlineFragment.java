package com.example.jntt.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class HeadlineFragment extends Fragment {

    private List<Article> allArticles;
    private List<Article> displayed;
    private ArticleAdapter adapter;
    private boolean searchExpanded = false;

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
        });
        rv.setAdapter(adapter);

        LinearLayout layoutSearch = view.findViewById(R.id.layoutSearch);
        EditText etSearch         = view.findViewById(R.id.etHeadlineSearch);
        TextView tvCancel         = view.findViewById(R.id.tvSearchCancel);
        LinearLayout layoutBrand  = view.findViewById(R.id.layoutBrand);

        layoutSearch.setOnClickListener(v -> {
            if (!searchExpanded) expandSearch(layoutSearch, etSearch, tvCancel, layoutBrand);
        });

        tvCancel.setOnClickListener(v -> {
            collapseSearch(layoutSearch, etSearch, tvCancel, layoutBrand);
            filter("");
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filter(s.toString().trim());
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { hideKeyboard(etSearch); return true; }
            return false;
        });
    }

    private void expandSearch(LinearLayout layout, EditText et, TextView cancel, LinearLayout brand) {
        searchExpanded = true;
        brand.setVisibility(View.INVISIBLE);
        int startW = layout.getWidth();
        int targetW = ((View) layout.getParent()).getWidth() - 32;
        ValueAnimator anim = ValueAnimator.ofInt(startW, targetW);
        anim.setDuration(260);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> { layout.getLayoutParams().width = (int) a.getAnimatedValue(); layout.requestLayout(); });
        anim.start();
        et.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    private void collapseSearch(LinearLayout layout, EditText et, TextView cancel, LinearLayout brand) {
        searchExpanded = false;
        hideKeyboard(et);
        et.setText("");
        et.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        int startW = layout.getWidth();
        int targetW = (int) (38 * getResources().getDisplayMetrics().density);
        ValueAnimator anim = ValueAnimator.ofInt(startW, targetW);
        anim.setDuration(240);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(a -> { layout.getLayoutParams().width = (int) a.getAnimatedValue(); layout.requestLayout(); });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator a) { brand.setVisibility(View.VISIBLE); }
        });
        anim.start();
    }

    private void filter(String query) {
        displayed.clear();
        if (query.isEmpty()) {
            displayed.addAll(allArticles);
        } else {
            for (Article a : allArticles) {
                if (a.title.contains(query) || a.content.contains(query) || a.author.contains(query))
                    displayed.add(a);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            allArticles.clear();
            allArticles.addAll(DataManager.getInstance(requireContext()).getArticles());
            filter("");
        }
    }
}
