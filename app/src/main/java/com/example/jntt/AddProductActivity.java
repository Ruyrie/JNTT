package com.example.jntt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jntt.data.DataManager;

/** 添加商品界面（仅管理员可见） */
public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle("添加商品");

        EditText etName  = findViewById(R.id.etProductName);
        EditText etDesc  = findViewById(R.id.etProductDesc);
        EditText etPrice = findViewById(R.id.etProductPrice);
        Button btnSubmit = findViewById(R.id.btnSubmitProduct);

        DataManager dm = DataManager.getInstance(this);

        btnSubmit.setOnClickListener(v -> {
            String name  = etName.getText().toString().trim();
            String desc  = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "所有字段不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            double price;
            try { price = Double.parseDouble(priceStr); }
            catch (NumberFormatException e) {
                Toast.makeText(this, "价格格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            dm.addProduct(name, desc, price);
            Toast.makeText(this, "商品添加成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
