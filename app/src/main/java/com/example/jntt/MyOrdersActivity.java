package com.example.jntt;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.OrderAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Order;
import java.util.List;

/** 我的订单界面：显示当前账号的购买记录 */
public class MyOrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        setTitle("我的订单");

        DataManager dm = DataManager.getInstance(this);
        String username = dm.getLoggedUser();
        List<Order> orders = dm.getOrders(username);

        RecyclerView rv = findViewById(R.id.rvOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new OrderAdapter(orders));
    }
}
