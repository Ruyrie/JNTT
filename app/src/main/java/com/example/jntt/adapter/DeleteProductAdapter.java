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

/** 删除商品列表适配器（管理员）：每行展示商品并提供删除按钮，无加入购物车入口。 */
public class DeleteProductAdapter extends RecyclerView.Adapter<DeleteProductAdapter.VH> {

    public interface OnDeleteClickListener {
        void onDelete(Product product);
    }

    private final List<Product> data;
    private final OnDeleteClickListener deleteListener;

    public DeleteProductAdapter(List<Product> data, OnDeleteClickListener deleteListener) {
        this.data = data;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delete_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = data.get(position);
        ProductAdapter.bindProductImage(holder.ivProduct, p);
        holder.tvName.setText(p.name);
        holder.tvDesc.setText(p.desc);
        holder.tvPrice.setText(p.price == Math.floor(p.price)
                ? String.format("%,d", (long) p.price)
                : String.format("%,.2f", p.price));
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null)
                deleteListener.onDelete(p);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvDesc, tvPrice;
        View btnDelete;

        VH(View v) {
            super(v);
            ivProduct = v.findViewById(R.id.ivProductImage);
            tvName = v.findViewById(R.id.tvProductName);
            tvDesc = v.findViewById(R.id.tvProductDesc);
            tvPrice = v.findViewById(R.id.tvProductPrice);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
