package com.example.jntt;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jntt.adapter.CartAdapter;
import com.example.jntt.data.DataManager;
import com.example.jntt.model.CartItem;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private CartAdapter adapter;
    private List<CartItem> items;
    private CheckBox cbSelectAll;
    private TextView tvTotal, tvCount;
    private DataManager dm;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        dm = DataManager.getInstance(this);
        username = dm.getLoggedUser();
        items = dm.getCart(username);

        RecyclerView rv = findViewById(R.id.rvCart);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CartAdapter(items);
        adapter.setOnChangeListener(this::persistCartState);
        rv.setAdapter(adapter);

        cbSelectAll = findViewById(R.id.cbSelectAll);
        tvTotal = findViewById(R.id.tvCartTotal);
        tvCount = findViewById(R.id.tvCartCount);
        Button btnCheckout = findViewById(R.id.btnCheckout);
        TextView tvClear = findViewById(R.id.tvClearCart);

        cbSelectAll.setOnCheckedChangeListener((btn, checked) -> adapter.setAllChecked(checked));

        btnCheckout.setOnClickListener(v -> checkout());

        tvClear.setOnClickListener(v -> showClearCartDialog());

        refreshBottomBar();
    }

    private void showClearCartDialog() {
        if (items.isEmpty()) {
            Toast.makeText(this, "购物车已经是空的", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_clear_cart);
        dialog.setCanceledOnTouchOutside(true);

        TextView btnCancel = dialog.findViewById(R.id.btnClearCancel);
        TextView btnConfirm = dialog.findViewById(R.id.btnClearConfirm);
        TextView tvMessage = dialog.findViewById(R.id.tvClearMessage);
        tvMessage.setText("将移除当前 " + items.size() + " 件商品，退出后也不会恢复。");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            adapter.clearAll();
            dialog.dismiss();
            Toast.makeText(this, "购物车已清空", Toast.LENGTH_SHORT).show();
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

    private void persistCartState() {
        dm.saveCartPublic(username, items);
        refreshBottomBar();
    }

    private void refreshBottomBar() {
        double total = adapter.getSelectedTotal();
        tvTotal.setText(String.format("¥%.2f", total));
        tvCount.setText("共 " + items.size() + " 件");
        cbSelectAll.setOnCheckedChangeListener(null);
        cbSelectAll.setChecked(adapter.areAllChecked());
        cbSelectAll.setOnCheckedChangeListener((btn, checked) -> adapter.setAllChecked(checked));
    }

    private void checkout() {
        List<com.example.jntt.model.CartItem> checkedItems = adapter.getCheckedItems();
        if (checkedItems.isEmpty()) {
            Toast.makeText(this, "请先选择商品", Toast.LENGTH_SHORT).show();
            return;
        }
        for (com.example.jntt.model.CartItem item : checkedItems) {
            dm.addOrder(username, item.productId, item.name, item.price, item.quantity);
        }
        adapter.removeChecked();
        dm.saveCartPublic(username, items);
        startActivity(new Intent(this, MyOrdersActivity.class));
        finish();
    }
}
