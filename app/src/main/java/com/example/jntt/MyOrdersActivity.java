package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.OrderAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Order;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private List<Order> orders;
    private OrderAdapter adapter;
    private DataManager dm;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        dm = DataManager.getInstance(this);
        username = dm.getLoggedUser();
        orders = dm.getOrders(username);

        RecyclerView rv = findViewById(R.id.rvOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderAdapter(orders);

        adapter.setOnItemClickListener(order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("order_id", order.orderId);
            startActivity(intent);
        });

        adapter.setOnActionListener(new OrderAdapter.OnActionListener() {
            @Override public void onPay(Order order) {
                dm.updateOrderStatus(username, order.orderId, Order.STATUS_PAID);
                refresh();
                android.widget.Toast.makeText(MyOrdersActivity.this, "支付成功！", android.widget.Toast.LENGTH_SHORT).show();
            }
            @Override public void onCancel(Order order) {
                new AlertDialog.Builder(MyOrdersActivity.this)
                    .setTitle("取消订单").setMessage("确定取消此订单吗？")
                    .setPositiveButton("确定", (d, w) -> {
                        dm.updateOrderStatus(username, order.orderId, Order.STATUS_CANCELLED);
                        refresh();
                    })
                    .setNegativeButton("取消", null).show();
            }
        });

        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() { super.onResume(); refresh(); }

    private void refresh() {
        orders.clear();
        orders.addAll(dm.getOrders(username));
        adapter.notifyDataSetChanged();
    }
}
