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
/**
 * 项目职责：商品详情页，负责商品图片、价格、详情、评价摘要、购物车和购买入口。
 * 技术说明：绑定布局控件；绑定点击事件；提示用户操作结果。
 * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
 */
public class ProductDetailActivity extends AppCompatActivity {

    private int productId;
    private DataManager dm;

    /**
     * 项目职责：初始化商品详情页，负责商品图片、价格、详情、评价摘要、购物车和购买入口，加载布局、读取业务数据并绑定用户操作。
     * 关键调用：绑定布局控件；显示内置图片资源；显示用户选择的图片 URI；连接 RecyclerView 与 Adapter。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        productId = getIntent().getIntExtra("product_id", -1);
        dm = DataManager.getInstance(this);

        // 查找商品
        Product target = null;
        for (Product p : dm.getProducts()) {
            if (p.id == productId) {
                target = p;
                break;
            }
        }
        if (target == null) {
            finish();
            return;
        }

        final Product product = target;
        String username = dm.getLoggedUser();

        androidx.recyclerview.widget.RecyclerView vpProductImage = findViewById(R.id.vpProductImage);
        TextView tvImageIndicator = findViewById(R.id.tvImageIndicator);

        java.util.List<Object> images = new java.util.ArrayList<>();

        switch (product.id) {
            case 1:
                images.add(R.mipmap.dami1);
                images.add(R.mipmap.dami2);
                images.add(R.mipmap.dami3);
                break;
            case 2:
                images.add(R.mipmap.muer);
                images.add(R.mipmap.muer2);
                images.add(R.mipmap.muer3);
                images.add(R.mipmap.muer4);
                images.add(R.mipmap.muer5);
                break;
            case 3:
                images.add(R.mipmap.fengmi1);
                images.add(R.mipmap.fengmi2);
                images.add(R.mipmap.fengmi3);
                break;
            case 4:
                images.add(R.mipmap.shucai1);
                images.add(R.mipmap.shucai2);
                images.add(R.mipmap.shucai3);
                images.add(R.mipmap.shucai4);
                break;
            case 5:
                images.add(R.mipmap.dongchongxiacao1);
                images.add(R.mipmap.dongchongxiacao2);
                images.add(R.mipmap.dongchongxiacao3);
                break;
            case 6:
                images.add(R.mipmap.hongshu1);
                images.add(R.mipmap.hongshu2);
                images.add(R.mipmap.hongshu3);
                images.add(R.mipmap.hongshu4);
                break;
            case 7:
                images.add(R.mipmap.shanyao1);
                images.add(R.mipmap.shanyao2);
                images.add(R.mipmap.shanyao3);
                break;
            case 8:
                images.add(R.mipmap.yangdujun1);
                images.add(R.mipmap.yangdujun2);
                break;
            case 9:
                images.add(R.mipmap.luronggu1);
                images.add(R.mipmap.luronggu2);
                images.add(R.mipmap.luronggu3);
                images.add(R.mipmap.luronggu4);
                break;
            case 10:
                images.add(R.mipmap.tuedan1);
                images.add(R.mipmap.tuedan2);
                images.add(R.mipmap.tuedan3);
                images.add(R.mipmap.tuedan4);
                break;
            default:
                if (product.coverUri != null && !product.coverUri.isEmpty()) {
                    String[] uris = product.coverUri.split(",");
                    for (String uri : uris) {
                        images.add(uri);
                    }
                } else {
                    images.add(R.drawable.ic_product_placeholder);
                }
        }

        com.example.jntt.adapter.ProductImageAdapter imageAdapter = new com.example.jntt.adapter.ProductImageAdapter(
                images);
        androidx.recyclerview.widget.LinearLayoutManager imageLayoutManager =
                new androidx.recyclerview.widget.LinearLayoutManager(this,
                        androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false);
        vpProductImage.setLayoutManager(imageLayoutManager);
        vpProductImage.setAdapter(imageAdapter);
        new androidx.recyclerview.widget.PagerSnapHelper().attachToRecyclerView(vpProductImage);

        if (images.size() <= 1) {
            tvImageIndicator.setVisibility(android.view.View.GONE);
        } else {
            tvImageIndicator.setVisibility(android.view.View.VISIBLE);
            tvImageIndicator.setText("1/" + images.size());
        }

        vpProductImage.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            /**
             * 项目职责：商品详情页，负责商品图片、价格、详情、评价摘要、购物车和购买入口。
             * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
             * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
             */
            @Override
            public void onScrollStateChanged(@androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (images.size() > 1 && newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
                    int position = imageLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (position == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        position = imageLayoutManager.findFirstVisibleItemPosition();
                    }
                    tvImageIndicator.setText((position + 1) + "/" + images.size());
                }
            }
        });

        ((TextView) findViewById(R.id.tvDetailProductName)).setText(product.name);
        ((TextView) findViewById(R.id.tvDetailProductDesc)).setText(product.desc);
        ((TextView) findViewById(R.id.tvDetailProductPrice))
                .setText(String.format("¥%,.2f", product.price));

        // 绑定底部图文详情 RecyclerView
        androidx.recyclerview.widget.RecyclerView rvDetailImages = findViewById(R.id.rvDetailImages);
        rvDetailImages.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        rvDetailImages.setAdapter(
                new androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
                    /**
                     * 项目职责：为商品详情页创建 RecyclerView 列表项 ViewHolder。
                     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                     * 配合代码：配合当前模块的布局、数据类和调用方使用。
                     */
                    @androidx.annotation.NonNull
                    @Override
                    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(
                            @androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
                        ImageView iv = new ImageView(parent.getContext());
                        iv.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                        iv.setAdjustViewBounds(true);
                        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        return new androidx.recyclerview.widget.RecyclerView.ViewHolder(iv) {
                        };
                    }

                    /**
                     * 项目职责：把当前位置的数据绑定到商品详情页的 item 布局控件上。
                     * 关键调用：显示内置图片资源；显示用户选择的图片 URI。
                     * 配合代码：配合当前模块的布局、数据类和调用方使用。
                     */
                    @Override
                    public void onBindViewHolder(
                            @androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder holder,
                            int position) {
                        ImageView iv = (ImageView) holder.itemView;
                        Object item = images.get(position);
                        if (item instanceof Integer) {
                            iv.setImageResource((Integer) item);
                        } else if (item instanceof String) {
                            try {
                                iv.setImageURI(android.net.Uri.parse((String) item));
                            } catch (Exception e) {
                                iv.setImageResource(R.drawable.ic_product_placeholder);
                            }
                        }
                    }

                    /**
                     * 项目职责：返回商品详情页当前列表需要展示的条目数量。
                     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
                     * 配合代码：配合当前模块的布局、数据类和调用方使用。
                     */
                    @Override
                    public int getItemCount() {
                        return images.size();
                    }
                });

        // 评价模块
        android.widget.LinearLayout llCommentSection = findViewById(R.id.llCommentSection);
        llCommentSection.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductCommentsActivity.class);
            intent.putExtra("product_id", product.id);
            startActivity(intent);
        });

        // 加入购物车
        ((Button) findViewById(R.id.btnAddCart)).setOnClickListener(v -> {
            dm.addToCart(username, product);
            Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
        });

        // 立即购买 → 生成待支付订单并跳转到订单列表
        ((Button) findViewById(R.id.btnBuy)).setOnClickListener(v -> {
            dm.addOrder(username, product.id, product.name, product.price, 1);
            Toast.makeText(this, "下单成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MyOrdersActivity.class));
        });

        // 购物车图标
        findViewById(R.id.ivCartIcon).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }

    /**
     * 项目职责：商品详情页，负责商品图片、价格、详情、评价摘要、购物车和购买入口回到前台时重新读取数据库数据并刷新显示。
     * 关键调用：使用 Java/Android 基础语法完成该业务步骤。
     * 配合代码：配合 AndroidManifest、activity_*.xml、DataManager 和页面跳转使用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (dm != null && productId != -1) {
            loadCommentPreview();
        }
    }

    /**
     * 项目职责：商品详情页，负责商品图片、价格、详情、评价摘要、购物车和购买入口。
     * 关键调用：绑定布局控件。
     * 配合代码：配合当前页面布局、DataManager 和相关 Adapter 使用。
     */
    private void loadCommentPreview() {
        int count = dm.getProductCommentCount(productId);
        TextView tvCommentCountTitle = findViewById(R.id.tvCommentCountTitle);
        tvCommentCountTitle.setText("商品评价 (" + count + ")");

        android.widget.LinearLayout llLatestComment = findViewById(R.id.llLatestComment);
        if (count > 0) {
            java.util.List<com.example.jntt.model.ProductComment> comments = dm.getProductComments(productId);
            if (!comments.isEmpty()) {
                com.example.jntt.model.ProductComment latest = comments.get(0);
                llLatestComment.setVisibility(android.view.View.VISIBLE);

                TextView tvUsername = findViewById(R.id.tvCommentUsername);
                TextView tvContent = findViewById(R.id.tvCommentContent);
                ImageView ivAvatar = findViewById(R.id.ivCommentAvatar);

                tvUsername.setText(
                        latest.nickname != null && !latest.nickname.isEmpty() ? latest.nickname : latest.username);
                tvContent.setText(latest.content);

                if (latest.avatarUri != null && !latest.avatarUri.isEmpty()) {
                    try {
                        ivAvatar.setImageURI(android.net.Uri.parse(latest.avatarUri));
                    } catch (Exception e) {
                        ivAvatar.setImageResource(R.mipmap.ic_launcher_round);
                    }
                } else {
                    ivAvatar.setImageResource(R.mipmap.ic_launcher_round);
                }
            }
        } else {
            llLatestComment.setVisibility(android.view.View.GONE);
        }
    }
}
