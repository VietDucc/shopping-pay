package com.example.uitpay.ui.invoice;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uitpay.R;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<InvoiceModel> invoices = new ArrayList<>();
    private InvoiceModel invoiceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.invoiceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo InvoiceModel
        invoiceModel = new InvoiceModel();

        // Gọi phương thức loadInvoiceData() để tải hóa đơn
        String userId = "phampho1103";

        invoiceModel.loadInvoiceData(userId, new InvoiceModel.InvoiceDataCallback() {
            @Override
            public void onSuccess(List<InvoiceModel> invoiceModels) {
                // Cập nhật danh sách hóa đơn
                invoices = invoiceModels;
                recyclerView.setAdapter(new InvoiceAdapter(invoices));
            }

            @Override
            public void onFailure(Exception e) {
                // Hiển thị lỗi nếu không lấy được dữ liệu
                Toast.makeText(InvoiceActivity.this, "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        setBackButton();
    }

    public void setBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();  // Quay lại Activity trước đó
        });
    }

    public String getFormattedDate(Timestamp datetime){
        Date date = datetime.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    // Tạo một Adapter bên trong Activity
    public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

        private List<InvoiceModel> invoices;

        public InvoiceAdapter(List<InvoiceModel> invoices) {
            this.invoices = invoices;
        }

        @Override
        public int getItemCount() {
            return invoices.size();
        }

        public class InvoiceViewHolder extends RecyclerView.ViewHolder {
            TextView invoiceIdTextView;
            TextView dateTextView;
            TextView costTextView;
            RecyclerView productRecyclerView;

            public InvoiceViewHolder(View itemView) {
                super(itemView);
                invoiceIdTextView = itemView.findViewById(R.id.invoiceNumberText);
                dateTextView = itemView.findViewById(R.id.createdAtText);
                costTextView = itemView.findViewById(R.id.costText);
                productRecyclerView = itemView.findViewById(R.id.productRecyclerView);
            }
        }

        @Override
        public InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item, parent, false);
            return new InvoiceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(InvoiceViewHolder holder, int position) {
            InvoiceModel invoice = invoices.get(position);
            holder.invoiceIdTextView.setText(invoice.getInvoiceID());
            holder.dateTextView.setText(getFormattedDate(invoice.getDate()));
            holder.costTextView.setText(invoice.getCost());
            List<Map<String, Object>> productList = new ArrayList<>();
            for (String productKey : invoice.getProductKeys()) {
                Map<String, Object> product = new HashMap<>();
                product.put("name", invoice.getProductName(productKey));
                product.put("price", invoice.getProductPrice(productKey));
                product.put("quantity", invoice.getProductQuantity(productKey));
                productList.add(product);
            }
            ProductAdapter productAdapter = new ProductAdapter(productList);
            holder.productRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.productRecyclerView.setAdapter(productAdapter);
        }
        // Adapter cho sản phẩm
        public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

            private List<Map<String, Object>> productList;

            public ProductAdapter(List<Map<String, Object>> productList) {
                this.productList = productList;
            }

            @Override
            public int getItemCount() {
                return productList.size();
            }

            public class ProductViewHolder extends RecyclerView.ViewHolder {
                TextView productNameTextView;
                TextView productPriceTextView;
                TextView productQuantityTextView;

                public ProductViewHolder(View itemView) {
                    super(itemView);
                    productNameTextView = itemView.findViewById(R.id.productName);
                    productPriceTextView = itemView.findViewById(R.id.productPrice);
                    productQuantityTextView = itemView.findViewById(R.id.productQuantity);
                }
            }

            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ProductViewHolder holder, int position) {
                Map<String, Object> product = productList.get(position);
                holder.productNameTextView.setText((String) product.get("name"));
                holder.productPriceTextView.setText((String) product.get("price"));
                holder.productQuantityTextView.setText((String) product.get("quantity"));
            }
        }
    }
}