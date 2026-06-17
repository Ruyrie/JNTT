package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.EditProductAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Product;
import java.util.ArrayList;
import java.util.List;

/** 编辑商品界面（仅管理员可见）：选择一个商品进入编辑表单。 */
public class EditProductActivity extends AppCompatActivity {

    private DataManager dm;
    private List<Product> products;
    private EditProductAdapter adapter;
    private RecyclerView rv;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        dm = DataManager.getInstance(this);

        // 编辑功能仅在管理员模式下可用
        if (!dm.isAdminMode()) {
            Toast.makeText(this, "仅管理员可编辑商品", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        rv = findViewById(R.id.rvEditProducts);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(this));

        products = new ArrayList<>();
        adapter = new EditProductAdapter(products, this::openEdit);
        rv.setAdapter(adapter);
    }

    private void openEdit(Product product) {
        Intent intent = new Intent(this, EditProductFormActivity.class);
        intent.putExtra("product_id", product.id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 离开编辑表单返回后刷新列表，反映最新的名称/价格/图片
        products.clear();
        products.addAll(dm.getProducts());
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean empty = products.isEmpty();
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }
}
