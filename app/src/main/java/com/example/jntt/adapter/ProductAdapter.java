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

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public interface OnAddCartListener {
        void onAddCart(Product product);
    }

    private final List<Product> data;
    private final OnItemClickListener clickListener;
    private OnAddCartListener addCartListener;

    public ProductAdapter(List<Product> data, OnItemClickListener listener) {
        this.data = data;
        this.clickListener = listener;
    }

    public void setOnAddCartListener(OnAddCartListener l) {
        this.addCartListener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    /** Resolve a product's thumbnail (seed products map to bundled images, others use cover_uri). */
    public static void bindProductImage(ImageView iv, Product p) {
        switch (p.id) {
            case 1:
                iv.setImageResource(R.mipmap.dami1);
                break; // 东北大米
            case 2:
                iv.setImageResource(R.mipmap.muer);
                break; // 有机黑木耳
            case 3:
                iv.setImageResource(R.mipmap.fengmi1);
                break; // 农家蜂蜜
            case 4:
                iv.setImageResource(R.mipmap.shucai1);
                break; // 绿色蔬菜礼盒
            case 5:
                iv.setImageResource(R.mipmap.dongchongxiacao1);
                break; // 冬虫夏草
            case 6:
                iv.setImageResource(R.mipmap.hongshu1);
                break; // 红薯
            case 7:
                iv.setImageResource(R.mipmap.shanyao1);
                break; // 山药
            case 8:
                iv.setImageResource(R.mipmap.yangdujun1);
                break; // 羊肚菌
            case 9:
                iv.setImageResource(R.mipmap.luronggu1);
                break; // 鹿茸菇
            case 10:
                iv.setImageResource(R.mipmap.tuedan1);
                break; // 土鹅蛋
            default:
                if (p.coverUri != null && !p.coverUri.isEmpty()) {
                    String firstUri = p.coverUri.split(",")[0];
                    try {
                        iv.setImageURI(android.net.Uri.parse(firstUri));
                    } catch (Exception e) {
                        iv.setImageResource(R.drawable.ic_product_placeholder);
                    }
                } else {
                    iv.setImageResource(R.drawable.ic_product_placeholder);
                }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = data.get(position);
        bindProductImage(holder.ivProduct, p);
        holder.tvName.setText(p.name);
        holder.tvDesc.setText(p.desc);
        // Show price without ¥ prefix (¥ is a separate TextView in XML)
        holder.tvPrice.setText(p.price == Math.floor(p.price)
                ? String.format("%,d", (long) p.price)
                : String.format("%,.2f", p.price));
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(p));
        holder.btnAddCart.setOnClickListener(v -> {
            if (addCartListener != null)
                addCartListener.onAddCart(p);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvDesc, tvPrice, btnAddCart;

        VH(View v) {
            super(v);
            ivProduct = v.findViewById(R.id.ivProductImage);
            tvName = v.findViewById(R.id.tvProductName);
            tvDesc = v.findViewById(R.id.tvProductDesc);
            tvPrice = v.findViewById(R.id.tvProductPrice);
            btnAddCart = v.findViewById(R.id.btnAddCart);
        }
    }
}
