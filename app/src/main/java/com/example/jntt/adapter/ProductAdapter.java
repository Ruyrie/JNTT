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

/**
 * 项目职责：商城商品 Adapter，负责把 Product 渲染成商品卡片，处理进入详情和加入购物车按钮。
 * 技术说明：绑定布局控件；绑定点击事件。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    /**
     * 项目职责：商品卡片点击回调接口，负责把 ProductAdapter 中的商品点击交给 MallFragment 打开详情页。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnItemClickListener {
        /**
         * 项目职责：把商品/文章/账号等列表项点击回调给页面处理详情跳转。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onItemClick(Product product);
    }

    /**
     * 项目职责：加入购物车回调接口，负责把 ProductAdapter 中的加购按钮交给 MallFragment 写入购物车。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnAddCartListener {
        /**
         * 项目职责：把商品卡片加购点击回调给 MallFragment 写入购物车。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onAddCart(Product product);
    }

    private final List<Product> data;
    private final OnItemClickListener clickListener;
    private OnAddCartListener addCartListener;

    /**
     * 项目职责：创建商城商品 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public ProductAdapter(List<Product> data, OnItemClickListener listener) {
        this.data = data;
        this.clickListener = listener;
    }

    /**
     * 项目职责：保存加入购物车回调，让商品卡片按钮点击后通知 MallFragment 写入购物车。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml、宿主 Activity/Fragment 的数据 List 使用。
     */
    public void setOnAddCartListener(OnAddCartListener l) {
        this.addCartListener = l;
    }

    /**
     * 项目职责：为商城商品 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    /**
     * 项目职责：根据商品 id 或 cover_uri 给商品卡片绑定正确图片，内置商品走 mipmap，自定义商品走 Uri。
     * 关键调用：显示内置图片资源；显示用户选择的图片 URI。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
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

    /**
     * 项目职责：把当前位置的数据绑定到商城商品 Adapter的 item 布局控件上。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
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

    /**
     * 项目职责：返回商城商品 Adapter当前列表需要展示的条目数量。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 项目职责：VH 对应的项目组件。
     * 技术说明：绑定布局控件。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
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
