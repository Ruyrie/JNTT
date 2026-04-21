package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.Product;
import java.util.List;

/** 商品列表适配器 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final List<Product> data;
    private final OnItemClickListener listener;

    public ProductAdapter(List<Product> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = data.get(position);
        // 图片使用固定占位图
        holder.ivProduct.setImageResource(R.drawable.ic_product_placeholder);
        holder.tvName.setText(p.name);
        holder.tvPrice.setText(String.format("¥%.2f", p.price));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(p));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice;
        VH(View v) {
            super(v);
            ivProduct = v.findViewById(R.id.ivProductImage);
            tvName    = v.findViewById(R.id.tvProductName);
            tvPrice   = v.findViewById(R.id.tvProductPrice);
        }
    }
}
