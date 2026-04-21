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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnItemClickListener { void onItemClick(Product product); }
    public interface OnAddCartListener  { void onAddCart(Product product); }

    private final List<Product> data;
    private final OnItemClickListener clickListener;
    private OnAddCartListener addCartListener;

    public ProductAdapter(List<Product> data, OnItemClickListener listener) {
        this.data = data;
        this.clickListener = listener;
    }

    public void setOnAddCartListener(OnAddCartListener l) { this.addCartListener = l; }

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
        switch (p.id) {
            case 1: holder.ivProduct.setImageResource(R.mipmap.dami);   break; // 东北大米
            case 2: holder.ivProduct.setImageResource(R.mipmap.muer);   break; // 有机黑木耳
            case 3: holder.ivProduct.setImageResource(R.mipmap.fengmi); break; // 农家蜂蜜
            case 4: holder.ivProduct.setImageResource(R.mipmap.shucai); break; // 绿色蔬菜礼盒
            default: holder.ivProduct.setImageResource(R.drawable.ic_product_placeholder);
        }
        holder.tvName.setText(p.name);
        holder.tvDesc.setText(p.desc);
        // Show price without ¥ prefix (¥ is a separate TextView in XML)
        holder.tvPrice.setText(String.valueOf((long) p.price % 1 == 0
                ? String.format("%d", (long) p.price)
                : String.format("%.2f", p.price)));
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(p));
        holder.btnAddCart.setOnClickListener(v -> {
            if (addCartListener != null) addCartListener.onAddCart(p);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvDesc, tvPrice, btnAddCart;
        VH(View v) {
            super(v);
            ivProduct  = v.findViewById(R.id.ivProductImage);
            tvName     = v.findViewById(R.id.tvProductName);
            tvDesc     = v.findViewById(R.id.tvProductDesc);
            tvPrice    = v.findViewById(R.id.tvProductPrice);
            btnAddCart = v.findViewById(R.id.btnAddCart);
        }
    }
}
