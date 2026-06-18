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
/**
 * 项目职责：编辑商品 Adapter，负责管理员商品编辑列表展示和编辑回调。
 * 技术说明：绑定布局控件；绑定点击事件。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class EditProductAdapter extends RecyclerView.Adapter<EditProductAdapter.VH> {

    /**
     * 项目职责：编辑按钮回调接口，负责把 Adapter 中的编辑点击交给页面打开编辑表单。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnEditClickListener {
        /**
         * 项目职责：把管理员商品列表的编辑点击回调给 EditProductActivity。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onEdit(Product product);
    }

    private final List<Product> data;
    private final OnEditClickListener editListener;

    /**
     * 项目职责：创建编辑商品 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public EditProductAdapter(List<Product> data, OnEditClickListener editListener) {
        this.data = data;
        this.editListener = editListener;
    }

    /**
     * 项目职责：为编辑商品 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_product, parent, false);
        return new VH(v);
    }

    /**
     * 项目职责：把当前位置的数据绑定到编辑商品 Adapter的 item 布局控件上。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
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

    /**
     * 项目职责：返回编辑商品 Adapter当前列表需要展示的条目数量。
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
