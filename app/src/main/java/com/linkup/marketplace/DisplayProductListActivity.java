package com.linkup.marketplace;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.linkup.marketplace.adapter.ProductListAdapter;
import com.linkup.marketplace.dao.ProductDao;
import com.linkup.marketplace.model.Product;
import java.util.List;

public class DisplayProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private List<Product> productList;
    private ProductDao dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product_list);

        recyclerView = findViewById(R.id.recyclerView);
        dbHelper = new ProductDao(this);
        productList = dbHelper.getAllProducts();

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int currentUserId = prefs.getInt("userId", -1); // এখানে session থেকে userId নিচ্ছি

        adapter = new ProductListAdapter(this, productList, currentUserId); // ✅ ৩টি argument পাঠানো হয়েছে
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
