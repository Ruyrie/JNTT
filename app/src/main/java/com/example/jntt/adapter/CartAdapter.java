package com.example.jntt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.CartItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    /**
     * 项目职责：购物车变化回调接口，负责把 CartAdapter 中数量/勾选/删除变化交给 CartActivity 保存。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnChangeListener {
        /**
         * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onChange();
    }

    private final List<CartItem> data;
    private final Set<Integer> checkedIds = new HashSet<>(); // use productId as key
    private Set<Integer> availableIds = null; // null = availability unknown, treat all as available
    private OnChangeListener listener;

    /**
     * 项目职责：创建购物车 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public CartAdapter(List<CartItem> data) {
        this.data = data;
    }

    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setOnChangeListener(OnChangeListener l) {
        this.listener = l;
    }

    /** Supply the set of product ids that still exist; delisted items are flagged "商品已下架". */
    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：刷新列表。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setAvailableIds(Set<Integer> ids) {
        this.availableIds = ids;
        notifyDataSetChanged();
    }

    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    private boolean isAvailable(CartItem c) {
        return availableIds == null || availableIds.contains(c.productId);
    }

    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：刷新列表。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setAllChecked(boolean val) {
        checkedIds.clear();
        if (val) {
            for (CartItem c : data)
                if (isAvailable(c)) // delisted items cannot be selected
                    checkedIds.add(c.productId);
        }
        notifyDataSetChanged();
        if (listener != null)
            listener.onChange();
    }

    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public boolean areAllChecked() {
        boolean anyAvailable = false;
        for (CartItem c : data) {
            if (!isAvailable(c))
                continue;
            anyAvailable = true;
            if (!checkedIds.contains(c.productId))
                return false;
        }
        return anyAvailable;
    }

    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public double getSelectedTotal() {
        double total = 0;
        for (CartItem c : data)
            if (checkedIds.contains(c.productId) && isAvailable(c))
                total += c.price * c.quantity;
        return total;
    }

    /** Return a snapshot of currently checked items */
    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public List<CartItem> getCheckedItems() {
        List<CartItem> result = new ArrayList<>();
        for (CartItem c : data)
            if (checkedIds.contains(c.productId))
                result.add(c);
        return result;
    }

    /** Remove checked items, return count */
    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：刷新列表。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public int removeChecked() {
        int before = data.size();
        data.removeIf(c -> checkedIds.contains(c.productId));
        checkedIds.clear();
        notifyDataSetChanged();
        return before - data.size();
    }

    /** Remove ALL items */
    /**
     * 项目职责：购物车 Adapter，负责把 CartItem 渲染成购物车行，处理勾选、数量加减、删除和下架提示。
     * 关键调用：刷新列表。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void clearAll() {
        data.clear();
        checkedIds.clear();
        notifyDataSetChanged();
        if (listener != null)
            listener.onChange();
    }

    /**
     * 项目职责：为购物车 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    /**
     * 项目职责：把当前位置的数据绑定到购物车 Adapter的 item 布局控件上。
     * 关键调用：显示内置图片资源。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItem c = data.get(position);
        holder.tvName.setText(c.name);
        holder.tvPrice.setText(String.format("¥%,.2f", c.price));
        holder.tvQty.setText(String.valueOf(c.quantity));

        boolean available = isAvailable(c);
        if (!available)
            checkedIds.remove(c.productId); // a delisted item can never stay selected
        holder.tvUnavailable.setVisibility(available ? View.GONE : View.VISIBLE);
        float alpha = available ? 1f : 0.45f;
        holder.tvName.setAlpha(alpha);
        holder.tvPrice.setAlpha(alpha);
        holder.ivImage.setAlpha(alpha);
        holder.cbItem.setEnabled(available);
        holder.btnMinus.setEnabled(available);
        holder.btnPlus.setEnabled(available);

        switch (c.productId) {
            case 1:
                holder.ivImage.setImageResource(R.mipmap.dami1);
                break;
            case 2:
                holder.ivImage.setImageResource(R.mipmap.muer);
                break;
            case 3:
                holder.ivImage.setImageResource(R.mipmap.fengmi1);
                break;
            case 4:
                holder.ivImage.setImageResource(R.mipmap.shucai1);
                break;
            case 5:
                holder.ivImage.setImageResource(R.mipmap.dongchongxiacao1);
                break;
            case 6:
                holder.ivImage.setImageResource(R.mipmap.hongshu1);
                break;
            case 7:
                holder.ivImage.setImageResource(R.mipmap.shanyao1);
                break;
            case 8:
                holder.ivImage.setImageResource(R.mipmap.yangdujun1);
                break;
            case 9:
                holder.ivImage.setImageResource(R.mipmap.luronggu1);
                break;
            case 10:
                holder.ivImage.setImageResource(R.mipmap.tuedan1);
                break;
            default:
                holder.ivImage.setImageResource(R.drawable.ic_product_placeholder);
        }

        holder.cbItem.setOnCheckedChangeListener(null);
        holder.cbItem.setChecked(checkedIds.contains(c.productId));
        holder.cbItem.setOnCheckedChangeListener((btn, checked) -> {
            if (checked)
                checkedIds.add(data.get(holder.getAdapterPosition()).productId);
            else
                checkedIds.remove(data.get(holder.getAdapterPosition()).productId);
            if (listener != null)
                listener.onChange();
        });

        holder.btnMinus.setOnClickListener(v -> {
            CartItem item = data.get(holder.getAdapterPosition());
            if (item.quantity > 1) {
                item.quantity--;
                notifyItemChanged(holder.getAdapterPosition());
            } else {
                checkedIds.remove(item.productId);
                data.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
            if (listener != null)
                listener.onChange();
        });

        holder.btnPlus.setOnClickListener(v -> {
            data.get(holder.getAdapterPosition()).quantity++;
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null)
                listener.onChange();
        });
    }

    /**
     * 项目职责：返回购物车 Adapter当前列表需要展示的条目数量。
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
        CheckBox cbItem;
        ImageView ivImage;
        TextView tvName, tvPrice, tvQty, btnMinus, btnPlus, tvUnavailable;

        VH(View v) {
            super(v);
            cbItem = v.findViewById(R.id.cbCartItem);
            ivImage = v.findViewById(R.id.ivCartImage);
            tvName = v.findViewById(R.id.tvCartName);
            tvPrice = v.findViewById(R.id.tvCartPrice);
            tvQty = v.findViewById(R.id.tvCartQty);
            btnMinus = v.findViewById(R.id.btnMinus);
            btnPlus = v.findViewById(R.id.btnPlus);
            tvUnavailable = v.findViewById(R.id.tvCartUnavailable);
        }
    }
}
