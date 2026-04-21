package com.example.jntt;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.CartAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.CartItem;
import java.util.List;

/** 购物车界面 */
public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("购物车");

        DataManager dm = DataManager.getInstance(this);
        String username = dm.getLoggedUser();
        List<CartItem> items = dm.getCart(username);

        RecyclerView rv = findViewById(R.id.rvCart);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CartAdapter(items));

        // 计算合计
        double total = 0;
        for (CartItem c : items) total += c.price * c.quantity;
        ((TextView) findViewById(R.id.tvCartTotal))
            .setText(String.format("合计：¥%.2f", total));
    }
}
