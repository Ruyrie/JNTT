package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Product;

/** 商品详情界面：图片、名称、介绍、价格、加入购物车/购买/购物车入口 */
public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        int productId = getIntent().getIntExtra("product_id", -1);
        DataManager dm = DataManager.getInstance(this);

        // 查找商品
        Product target = null;
        for (Product p : dm.getProducts()) {
            if (p.id == productId) { target = p; break; }
        }
        if (target == null) { finish(); return; }

        final Product product = target;
        String username = dm.getLoggedUser();

        ((ImageView) findViewById(R.id.ivDetailProductImage))
            .setImageResource(R.drawable.ic_product_placeholder);
        ((TextView) findViewById(R.id.tvDetailProductName)).setText(product.name);
        ((TextView) findViewById(R.id.tvDetailProductDesc)).setText(product.desc);
        ((TextView) findViewById(R.id.tvDetailProductPrice))
            .setText(String.format("¥%.2f", product.price));

        // 加入购物车
        ((Button) findViewById(R.id.btnAddCart)).setOnClickListener(v -> {
            dm.addToCart(username, product);
            Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
        });

        // 立即购买
        ((Button) findViewById(R.id.btnBuy)).setOnClickListener(v -> {
            dm.addOrder(username, product);
            Toast.makeText(this, "购买成功", Toast.LENGTH_SHORT).show();
        });

        // 购物车图标
        findViewById(R.id.ivCartIcon).setOnClickListener(v ->
            startActivity(new Intent(this, CartActivity.class)));
    }
}
