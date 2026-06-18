package com.example.jntt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.ProductCommentAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.ProductComment;
import java.util.List;

/**
 * 项目职责：商品评价列表页，负责评价列表、发布评价入口和删除本人评价。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class ProductCommentsActivity extends AppCompatActivity {

    private int productId;
    private DataManager dm;
    private String currentUser;
    private RecyclerView rvComments;
    private TextView tvEmptyHint;
    private ProductCommentAdapter adapter;
    private List<ProductComment> comments;

    /**
     * 项目职责：初始化商品评价列表页，负责评价列表、发布评价入口和删除本人评价，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；设置列表排列方式；读写本地业务数据；页面跳转或传递参数。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_comments);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        productId = getIntent().getIntExtra("product_id", -1);
        dm = DataManager.getInstance(this);
        currentUser = dm.getLoggedUser();

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        TextView tvWriteReview = findViewById(R.id.tvWriteReview);
        tvWriteReview.setVisibility(View.VISIBLE);
        tvWriteReview.setOnClickListener(v -> {
            if (dm.hasPurchasedProduct(currentUser, productId) || "admin".equals(currentUser)) {
                Intent intent = new Intent(this, AddProductCommentActivity.class);
                intent.putExtra("product_id", productId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "购买该商品后才可以进行评价哦", Toast.LENGTH_SHORT).show();
            }
        });

        rvComments = findViewById(R.id.rvComments);
        tvEmptyHint = findViewById(R.id.tvEmptyHint);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 项目职责：商品评价列表页，负责评价列表、发布评价入口和删除本人评价回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadComments();
    }

    /**
     * 项目职责：商品评价列表页，负责评价列表、发布评价入口和删除本人评价。
     * 关键调用：绑定布局控件；绑定点击事件。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void loadComments() {
        comments = dm.getProductComments(productId);
        if (comments.isEmpty()) {
            tvEmptyHint.setVisibility(View.VISIBLE);
            rvComments.setVisibility(View.GONE);
        } else {
            tvEmptyHint.setVisibility(View.GONE);
            rvComments.setVisibility(View.VISIBLE);
            adapter = new ProductCommentAdapter(comments, currentUser);
            adapter.setOnDeleteListener(comment -> {
                android.view.View view = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
                android.widget.TextView tvMessage = view.findViewById(R.id.tvDialogMessage);
                tvMessage.setText("确定删除这条评价吗？");

                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(view)
                        .create();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                view.findViewById(R.id.btnDialogCancel).setOnClickListener(btn -> dialog.dismiss());

                android.widget.TextView btnConfirm = view.findViewById(R.id.btnDialogConfirm);
                btnConfirm.setBackgroundResource(R.drawable.bg_auth_button);
                btnConfirm.setOnClickListener(btn -> {
                    dialog.dismiss();
                    dm.deleteProductComment(comment.id);
                    loadComments();
                });

                dialog.show();
            });
            rvComments.setAdapter(adapter);
        }
    }
}
