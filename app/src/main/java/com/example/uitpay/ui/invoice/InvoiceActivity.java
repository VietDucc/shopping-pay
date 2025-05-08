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
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            TextView productTextView;
            TextView quantityTextView;

            public InvoiceViewHolder(View itemView) {
                super(itemView);
                invoiceIdTextView = itemView.findViewById(R.id.invoiceNumberText);
                dateTextView = itemView.findViewById(R.id.createdAtText);
                costTextView = itemView.findViewById(R.id.costText);
                productTextView = itemView.findViewById(R.id.productText);
                quantityTextView = itemView.findViewById(R.id.quantityText);
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
            holder.productTextView.setText(invoice.getProduct());
            holder.quantityTextView.setText(invoice.getQuantity());
        }
    }
}