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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.CartActivity;
import com.example.jntt.ProductDetailActivity;
import com.example.jntt.R;
import com.example.jntt.adapter.ProductAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Product;
import java.util.ArrayList;
import java.util.List;

public class MallFragment extends Fragment {

    private List<Product> allProducts;
    private List<Product> displayed;
    private ProductAdapter adapter;
    private TextView tvBadge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DataManager dm = DataManager.getInstance(requireContext());

        allProducts = dm.getProducts();
        displayed   = new ArrayList<>(allProducts);

        RecyclerView rv = view.findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

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

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            allProducts.clear();
            allProducts.addAll(DataManager.getInstance(requireContext()).getProducts());
            filter("");
        }
    }
}
