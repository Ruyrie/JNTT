package com.example.jntt.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.R;
import com.example.jntt.model.Order;
import java.util.List;

/**
 * 项目职责：订单 Adapter，负责订单列表展示、状态展示、详情和评价入口。
 * 技术说明：绑定布局控件；绑定点击事件。
 * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    /**
     * 项目职责：商品卡片点击回调接口，负责把 ProductAdapter 中的商品点击交给 MallFragment 打开详情页。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnItemClickListener { void onClick(Order order); }
    /**
     * 项目职责：OnActionListener 对应的项目组件。
     * 技术说明：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public interface OnActionListener {
        /**
         * 项目职责：订单 Adapter，负责订单列表展示、状态展示、详情和评价入口。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onPay(Order order);
        /**
         * 项目职责：订单 Adapter，负责订单列表展示、状态展示、详情和评价入口。
         * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
         * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
         */
        void onCancel(Order order);
    }

    private final List<Order> data;
    private OnItemClickListener clickListener;
    private OnActionListener actionListener;
    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 项目职责：创建订单列表 Adapter，保存页面传入的数据列表和点击回调。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    public OrderAdapter(List<Order> data) { this.data = data; }
    /**
     * 项目职责：订单 Adapter，负责订单列表展示、状态展示、详情和评价入口。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setOnItemClickListener(OnItemClickListener l) { this.clickListener = l; }
    /**
     * 项目职责：订单 Adapter，负责订单列表展示、状态展示、详情和评价入口。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    public void setOnActionListener(OnActionListener l) { this.actionListener = l; }

    /**
     * 项目职责：为订单列表 Adapter创建 RecyclerView 列表项 ViewHolder。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new VH(v);
    }

    /**
     * 项目职责：把当前位置的数据绑定到订单列表 Adapter的 item 布局控件上。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Order o = data.get(position);
        holder.tvName.setText(o.name);
        holder.tvPrice.setText(String.format("¥%.2f", o.price));
        holder.tvQty.setText("x" + o.quantity);
        holder.tvTime.setText("下单：" + o.time);

        // 停止旧倒计时
        if (holder.countdownRunnable != null) {
            handler.removeCallbacks(holder.countdownRunnable);
            holder.countdownRunnable = null;
        }

        // 处理超时自动取消
        if (Order.STATUS_PENDING.equals(o.status) && o.getRemainingMs() <= 0) {
            o.status = Order.STATUS_CANCELLED;
        }

        switch (o.status) {
            case Order.STATUS_PENDING:
                styleStatus(holder, "待支付", 0xFFFF9500, 0x1AFF9500);
                holder.tvCountdown.setTextColor(0xFFFF9500);
                holder.layoutActions.setVisibility(View.VISIBLE);
                // 实时倒计时
                holder.countdownRunnable = new Runnable() {
                    @Override public void run() {
                        long rem = o.getRemainingMs();
                        if (rem <= 0) {
                            o.status = Order.STATUS_CANCELLED;
                            notifyItemChanged(holder.getAdapterPosition());
                            return;
                        }
                        long h = rem / 3600000, m = (rem % 3600000) / 60000, s = (rem % 60000) / 1000;
                        holder.tvCountdown.setText(String.format("剩余 %02d:%02d:%02d", h, m, s));
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.post(holder.countdownRunnable);
                break;
            case Order.STATUS_PAID:
                styleStatus(holder, "已完成", 0xFF34C759, 0x1A34C759);
                holder.tvCountdown.setText("交易成功");
                holder.tvCountdown.setTextColor(0xFF34C759);
                holder.layoutActions.setVisibility(View.GONE);
                break;
            case Order.STATUS_CANCELLED:
                styleStatus(holder, "已取消", 0xFF8A8A8A, 0x1A8A8A8A);
                holder.tvCountdown.setText("订单已取消");
                holder.tvCountdown.setTextColor(0xFF8A8A8A);
                holder.layoutActions.setVisibility(View.GONE);
                break;
        }

        holder.btnPay.setOnClickListener(v -> { if (actionListener != null) actionListener.onPay(o); });
        holder.btnCancel.setOnClickListener(v -> { if (actionListener != null) actionListener.onCancel(o); });
        holder.itemView.setOnClickListener(v -> { if (clickListener != null) clickListener.onClick(o); });
    }

    /**
     * 项目职责：订单 Adapter，负责订单列表展示、状态展示、详情和评价入口。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        if (holder.countdownRunnable != null) {
            handler.removeCallbacks(holder.countdownRunnable);
            holder.countdownRunnable = null;
        }
    }

    /**
     * 项目职责：订单 Adapter，负责订单列表展示、状态展示、详情和评价入口。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    private void styleStatus(VH h, String text, int textColor, int bgColor) {
        h.tvStatus.setText(text);
        h.tvStatus.setTextColor(textColor);
        h.tvStatus.setBackgroundColor(bgColor);
        // set corner via padding only (no shape)
        h.tvStatus.setPadding(dp(h.itemView.getContext(), 8), dp(h.itemView.getContext(), 2),
                              dp(h.itemView.getContext(), 8), dp(h.itemView.getContext(), 2));
    }

    /**
     * 项目职责：把 dp 单位转换成像素，保证自定义绘制在不同密度屏幕上尺寸一致。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：由对应页面或 Adapter 持有，用于把列表点击/变化回调到业务页面。
     */
    private int dp(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }

    /**
     * 项目职责：返回订单列表 Adapter当前列表需要展示的条目数量。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 RecyclerView、item_*.xml 和宿主页面的数据列表使用。
     */
    @Override
    public int getItemCount() { return data.size(); }

    /**
     * 项目职责：VH 对应的项目组件。
     * 技术说明：绑定布局控件。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQty, tvTime, tvCountdown, tvStatus;
        TextView btnPay, btnCancel;
        LinearLayout layoutActions;
        Runnable countdownRunnable;
        VH(View v) {
            super(v);
            tvName       = v.findViewById(R.id.tvOrderName);
            tvPrice      = v.findViewById(R.id.tvOrderPrice);
            tvQty        = v.findViewById(R.id.tvOrderQty);
            tvTime       = v.findViewById(R.id.tvOrderTime);
            tvCountdown  = v.findViewById(R.id.tvOrderCountdown);
            tvStatus     = v.findViewById(R.id.tvOrderStatus);
            layoutActions= v.findViewById(R.id.layoutOrderActions);
            btnPay       = v.findViewById(R.id.btnPayOrder);
            btnCancel    = v.findViewById(R.id.btnCancelOrder);
        }
    }
}
