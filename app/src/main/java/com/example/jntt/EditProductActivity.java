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
/**
 * 项目职责：管理员编辑商品入口页，负责商品列表展示和编辑表单跳转。
 * 技术说明：绑定布局控件；绑定点击事件；刷新列表；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class EditProductActivity extends AppCompatActivity {

    private DataManager dm;
    private List<Product> products;
    private EditProductAdapter adapter;
    private RecyclerView rv;
    private TextView tvEmpty;

    /**
     * 项目职责：初始化管理员编辑商品入口页，负责商品列表展示和编辑表单跳转，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；连接 RecyclerView 与 Adapter；设置列表排列方式；读写本地业务数据。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
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

    /**
     * 项目职责：管理员编辑商品入口页，负责商品列表展示和编辑表单跳转。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void openEdit(Product product) {
        Intent intent = new Intent(this, EditProductFormActivity.class);
        intent.putExtra("product_id", product.id);
        startActivity(intent);
    }

    /**
     * 项目职责：管理员编辑商品入口页，负责商品列表展示和编辑表单跳转回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：刷新列表。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 离开编辑表单返回后刷新列表，反映最新的名称/价格/图片
        products.clear();
        products.addAll(dm.getProducts());
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    /**
     * 项目职责：管理员编辑商品入口页，负责商品列表展示和编辑表单跳转。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void updateEmptyState() {
        boolean empty = products.isEmpty();
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }
}
