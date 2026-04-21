package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.Order;
import java.util.List;

/** 订单列表适配器 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    private final List<Order> data;

    public OrderAdapter(List<Order> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Order o = data.get(position);
        holder.tvName.setText(o.name);
        holder.tvPrice.setText(String.format("¥%.2f", o.price));
        holder.tvTime.setText(o.time);
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvTime;
        VH(View v) {
            super(v);
            tvName  = v.findViewById(R.id.tvOrderName);
            tvPrice = v.findViewById(R.id.tvOrderPrice);
            tvTime  = v.findViewById(R.id.tvOrderTime);
        }
    }
}
