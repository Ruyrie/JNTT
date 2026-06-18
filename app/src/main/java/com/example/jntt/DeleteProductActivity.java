package com.example.jntt;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.DeleteProductAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.Product;
import java.util.ArrayList;
import java.util.List;

/** 删除商品界面（仅管理员可见） */
/**
 * 项目职责：管理员删除商品页，负责商品列表展示、删除确认和删除数据库记录。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class DeleteProductActivity extends AppCompatActivity {

    private DataManager dm;
    private List<Product> products;
    private DeleteProductAdapter adapter;
    private RecyclerView rv;
    private TextView tvEmpty;

    /**
     * 项目职责：初始化管理员删除商品页，负责商品列表展示、删除确认和删除数据库记录，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；连接 RecyclerView 与 Adapter；设置列表排列方式；读写本地业务数据。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        dm = DataManager.getInstance(this);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        rv = findViewById(R.id.rvDeleteProducts);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(this));

        products = new ArrayList<>(dm.getProducts());
        adapter = new DeleteProductAdapter(products, this::confirmDelete);
        rv.setAdapter(adapter);

        updateEmptyState();
    }

    /**
     * 项目职责：管理员删除商品页，负责商品列表展示、删除确认和删除数据库记录。
     * 关键调用：绑定布局控件；绑定点击事件；提示用户操作结果。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void confirmDelete(Product product) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_clear_cart);
        dialog.setCanceledOnTouchOutside(true);

        TextView tvTitle = dialog.findViewById(R.id.tvClearTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvClearMessage);
        TextView btnCancel = dialog.findViewById(R.id.btnClearCancel);
        TextView btnConfirm = dialog.findViewById(R.id.btnClearConfirm);

        tvTitle.setText("删除商品");
        tvMessage.setText("将永久删除「" + product.name + "」，删除后不可恢复。");
        btnConfirm.setText("确认删除");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dm.deleteProduct(product.id);
            int index = products.indexOf(product);
            if (index >= 0) {
                products.remove(index);
                adapter.notifyItemRemoved(index);
            }
            dialog.dismiss();
            Toast.makeText(this, "商品已删除", Toast.LENGTH_SHORT).show();
            updateEmptyState();
        });

        dialog.setOnShowListener(d -> {
            Window shownWindow = dialog.getWindow();
            if (shownWindow != null) {
                shownWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                shownWindow.setLayout(
                        (int) (getResources().getDisplayMetrics().widthPixels * 0.86f),
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
        dialog.show();
    }

    /**
     * 项目职责：管理员删除商品页，负责商品列表展示、删除确认和删除数据库记录。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void updateEmptyState() {
        boolean empty = products.isEmpty();
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }
}
