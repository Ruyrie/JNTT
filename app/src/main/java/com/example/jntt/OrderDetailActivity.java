package com.example.jntt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Order;

public class OrderDetailActivity extends AppCompatActivity {

    private Order order;
    private DataManager dm;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        dm = DataManager.getInstance(this);
        username = dm.getLoggedUser();
        String orderId = getIntent().getStringExtra("order_id");

        for (Order o : dm.getOrders(username)) {
            if (o.orderId.equals(orderId)) { order = o; break; }
        }
        if (order == null) { finish(); return; }

        bind();
    }

    private void bind() {
        TextView tvStatus    = findViewById(R.id.tvDetailStatus);
        TextView tvCountdown = findViewById(R.id.tvDetailCountdown);
        ImageView ivProduct  = findViewById(R.id.ivDetailProduct);
        TextView tvName      = findViewById(R.id.tvDetailName);
        TextView tvPrice     = findViewById(R.id.tvDetailPrice);
        TextView tvQty       = findViewById(R.id.tvDetailQty);
        TextView tvOrderId   = findViewById(R.id.tvDetailOrderId);
        TextView tvTime      = findViewById(R.id.tvDetailTime);
        TextView tvTotal     = findViewById(R.id.tvDetailTotal);
        LinearLayout layoutActions = findViewById(R.id.layoutDetailActions);
        Button btnPay    = findViewById(R.id.btnDetailPay);
        Button btnCancel = findViewById(R.id.btnDetailCancel);

        tvName.setText(order.name);
        switch (order.productId) {
            case 1: ivProduct.setImageResource(R.mipmap.dami);   break;
            case 2: ivProduct.setImageResource(R.mipmap.muer);   break;
            case 3: ivProduct.setImageResource(R.mipmap.fengmi); break;
            case 4: ivProduct.setImageResource(R.mipmap.shucai); break;
            default: ivProduct.setImageResource(R.drawable.ic_product_placeholder);
        }
        tvPrice.setText(String.format("¥%.2f", order.price));
        tvQty.setText("x" + order.quantity);
        tvOrderId.setText(order.orderId);
        tvTime.setText(order.time);
        tvTotal.setText(String.format("¥%.2f", order.price * order.quantity));

        // 若 pending 且已超时，自动取消
        if (Order.STATUS_PENDING.equals(order.status) && order.getRemainingMs() <= 0) {
            dm.updateOrderStatus(username, order.orderId, Order.STATUS_CANCELLED);
            order.status = Order.STATUS_CANCELLED;
        }

        switch (order.status) {
            case Order.STATUS_PENDING:
                tvStatus.setText("待支付");
                long rem = order.getRemainingMs();
                long h = rem / 3600000, m = (rem % 3600000) / 60000;
                tvCountdown.setText(String.format("请在 %02d:%02d 内完成支付，逾期将自动取消", h, m));
                layoutActions.setVisibility(View.VISIBLE);
                break;
            case Order.STATUS_PAID:
                tvStatus.setText("已完成");
                tvCountdown.setText("感谢您的购买");
                break;
            case Order.STATUS_CANCELLED:
                tvStatus.setText("已取消");
                tvCountdown.setText("订单已取消");
                tvStatus.getParent();
                ((View) tvStatus.getParent()).setBackgroundColor(0xFF8A8A8A);
                break;
        }

        btnPay.setOnClickListener(v -> {
            dm.updateOrderStatus(username, order.orderId, Order.STATUS_PAID);
            Toast.makeText(this, "支付成功！", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancel.setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setTitle("取消订单")
                .setMessage("确定取消此订单吗？")
                .setPositiveButton("确定", (d, w) -> {
                    dm.updateOrderStatus(username, order.orderId, Order.STATUS_CANCELLED);
                    Toast.makeText(this, "订单已取消", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("取消", null).show()
        );
    }
}
