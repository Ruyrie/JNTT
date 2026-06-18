package com.example.jntt.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.jntt.CartActivity;
import com.example.jntt.ProductDetailActivity;
import com.example.jntt.R;
import com.example.jntt.adapter.ProductAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目职责：商城 Fragment，负责商品瀑布流、搜索过滤、加入购物车和购物车角标刷新。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class MallFragment extends Fragment {

    private List<Product> allProducts;
    private List<Product> displayed;
    private ProductAdapter adapter;
    private TextView tvBadge;

    /**
     * 项目职责：加载商城 Fragment，负责商品瀑布流、搜索过滤、加入购物车和购物车角标刷新对应的 Fragment 布局文件。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 MainActivity、fragment_*.xml、DataManager 和 Adapter 使用。
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mall, container, false);
    }

    /**
     * 项目职责：在商城 Fragment，负责商品瀑布流、搜索过滤、加入购物车和购物车角标刷新界面创建完成后绑定控件、数据列表和点击事件。
     * 关键调用：绑定布局控件；连接 RecyclerView 与 Adapter；设置列表排列方式；读写本地业务数据。
     * 配合代码：配合 MainActivity、fragment_*.xml、DataManager 和 Adapter 使用。
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DataManager dm = DataManager.getInstance(requireContext());

        allProducts = dm.getProducts();
        displayed   = new ArrayList<>(allProducts);

        RecyclerView rv = view.findViewById(R.id.rvProducts);
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new ProductAdapter(displayed, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.id);
            startActivity(intent);
        });
        adapter.setOnAddCartListener(product -> {
            dm.addToCart(dm.getLoggedUser(), product);
            Toast.makeText(getContext(), "已加入购物车", Toast.LENGTH_SHORT).show();
            refreshBadge(view, dm);
        });
        rv.setAdapter(adapter);

        // 搜索框过滤
        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            /**
             * 项目职责：商城 Fragment，负责商品瀑布流、搜索过滤、加入购物车和购物车角标刷新。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filter(s.toString().trim());
            }
        });

        // 悬浮购物车按钮
        View fabCart = view.findViewById(R.id.fabCart);
        tvBadge = view.findViewById(R.id.tvCartBadge);
        fabCart.setOnClickListener(v ->
                startActivity(new Intent(getContext(), CartActivity.class)));

        refreshBadge(view, dm);
    }

    /**
     * 项目职责：商城 Fragment，负责商品瀑布流、搜索过滤、加入购物车和购物车角标刷新。
     * 关键调用：刷新列表。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void filter(String query) {
        displayed.clear();
        if (query.isEmpty()) {
            displayed.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.name.contains(query) || p.desc.contains(query)) {
                    displayed.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 项目职责：商城 Fragment，负责商品瀑布流、搜索过滤、加入购物车和购物车角标刷新。
     * 关键调用：绑定布局控件。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void refreshBadge(View root, DataManager dm) {
        String user = dm.getLoggedUser();
        if (user == null) return;
        int count = dm.getCart(user).size();
        TextView badge = root.findViewById(R.id.tvCartBadge);
        if (badge != null) {
            if (count > 0) {
                badge.setText(String.valueOf(count));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 项目职责：商城 Fragment，负责商品瀑布流、搜索过滤、加入购物车和购物车角标刷新回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：读写本地业务数据。
     * 配合代码：配合 MainActivity、fragment_*.xml、DataManager 和 Adapter 使用。
     */
    @Override
    public void onResume() {
        super.onResume();
        DataManager dm = DataManager.getInstance(requireContext());
        if (adapter != null) {
            allProducts.clear();
            allProducts.addAll(dm.getProducts());
            filter("");
        }
        if (getView() != null) {
            refreshBadge(getView(), dm);
        }
    }
}
