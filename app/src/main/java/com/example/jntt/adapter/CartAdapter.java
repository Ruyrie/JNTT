package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.CartItem;
import java.util.List;

/** 购物车列表适配器 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    private final List<CartItem> data;

    public CartAdapter(List<CartItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItem c = data.get(position);
        holder.tvName.setText(c.name);
        holder.tvPrice.setText(String.format("¥%.2f", c.price));
        holder.tvQty.setText("x" + c.quantity);
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQty;
        VH(View v) {
            super(v);
            tvName  = v.findViewById(R.id.tvCartName);
            tvPrice = v.findViewById(R.id.tvCartPrice);
            tvQty   = v.findViewById(R.id.tvCartQty);
        }
    }
}
