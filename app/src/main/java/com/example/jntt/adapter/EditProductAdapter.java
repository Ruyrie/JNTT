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

/** 编辑商品列表适配器（管理员）：每行展示商品并提供编辑按钮，点击进入编辑界面。 */
public class EditProductAdapter extends RecyclerView.Adapter<EditProductAdapter.VH> {

    public interface OnEditClickListener {
        void onEdit(Product product);
    }

    private final List<Product> data;
    private final OnEditClickListener editListener;

    public EditProductAdapter(List<Product> data, OnEditClickListener editListener) {
        this.data = data;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_product, parent, false);
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
        View.OnClickListener edit = v -> {
            if (editListener != null)
                editListener.onEdit(p);
        };
        holder.btnEdit.setOnClickListener(edit);
        holder.itemView.setOnClickListener(edit);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvDesc, tvPrice;
        View btnEdit;

        VH(View v) {
            super(v);
            ivProduct = v.findViewById(R.id.ivProductImage);
            tvName = v.findViewById(R.id.tvProductName);
            tvDesc = v.findViewById(R.id.tvProductDesc);
            tvPrice = v.findViewById(R.id.tvProductPrice);
            btnEdit = v.findViewById(R.id.btnEdit);
        }
    }
}
