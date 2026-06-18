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

/**
 * 项目职责：我的订单页，负责当前用户订单列表和详情跳转。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class MyOrdersActivity extends AppCompatActivity {

    private List<Order> orders;
    private OrderAdapter adapter;
    private DataManager dm;
    private String username;

    /**
     * 项目职责：初始化我的订单页，负责当前用户订单列表和详情跳转，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：加载列表项 XML 布局；加载 XML 布局；绑定布局控件；连接 RecyclerView 与 Adapter。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        dm = DataManager.getInstance(this);
        username = dm.getLoggedUser();
        orders = dm.getOrders(username);

        RecyclerView rv = findViewById(R.id.rvOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        adapter = new OrderAdapter(orders);

        adapter.setOnItemClickListener(order -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("order_id", order.orderId);
            startActivity(intent);
        });

        adapter.setOnActionListener(new OrderAdapter.OnActionListener() {
            /**
             * 项目职责：我的订单页，负责当前用户订单列表和详情跳转。
             * 关键调用：提示用户操作结果。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onPay(Order order) {
                dm.updateOrderStatus(username, order.orderId, Order.STATUS_PAID);
                refresh();
                android.widget.Toast.makeText(MyOrdersActivity.this, "支付成功！", android.widget.Toast.LENGTH_SHORT).show();
            }

            /**
             * 项目职责：我的订单页，负责当前用户订单列表和详情跳转。
             * 关键调用：绑定布局控件；绑定点击事件。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onCancel(Order order) {
                android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                android.widget.TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
                android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
                tvTitle.setText("取消订单");
                tvMessage.setText("确定取消此订单吗？");

                AlertDialog dialog = new AlertDialog.Builder(MyOrdersActivity.this)
                        .setView(view)
                        .create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                view.findViewById(R.id.btnDialogCancel).setOnClickListener(v -> dialog.dismiss());
                view.findViewById(R.id.btnDialogConfirm).setOnClickListener(v -> {
                    dialog.dismiss();
                    dm.updateOrderStatus(username, order.orderId, Order.STATUS_CANCELLED);
                    refresh();
                });
                dialog.show();
            }
        });

        rv.setAdapter(adapter);
    }

    /**
     * 项目职责：我的订单页，负责当前用户订单列表和详情跳转回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 项目职责：我的订单页，负责当前用户订单列表和详情跳转。
     * 关键调用：刷新列表。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void refresh() {
        orders.clear();
        orders.addAll(dm.getOrders(username));
        adapter.notifyDataSetChanged();
    }
}
