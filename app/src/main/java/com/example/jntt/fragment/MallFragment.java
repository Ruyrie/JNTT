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
import com.example.jntt.ProductDetailActivity;
import com.example.jntt.R;
import com.example.jntt.adapter.ProductAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Product;
import java.util.List;

/** 商城 Fragment：显示商品列表 */
public class MallFragment extends Fragment {

    private List<Product> products;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        DataManager dm = DataManager.getInstance(requireContext());
        products = dm.getProducts();

        adapter = new ProductAdapter(products, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.id);
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 返回时刷新列表
        if (adapter != null) {
            products.clear();
            products.addAll(DataManager.getInstance(requireContext()).getProducts());
            adapter.notifyDataSetChanged();
        }
    }
}
