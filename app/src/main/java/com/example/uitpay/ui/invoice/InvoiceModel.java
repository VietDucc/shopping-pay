package com.example.uitpay.ui.invoice;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class InvoiceModel {
    private Long cost;
    private Timestamp date;
    private String invoiceId;
    private String product;
    private Number quantity;
    private FirebaseFirestore db;

    public InvoiceModel() {
        db = FirebaseFirestore.getInstance();
    }

    public InvoiceModel(Long cost, Timestamp date, String invoiceId, String product, Number quantity) {
        this.cost = cost;
        this.date = date;
        this.invoiceId = invoiceId;
        this.product = product;
        this.quantity = quantity;
    }

    public String getCost() {
        return cost + " VND";
    }
    public String getInvoiceID() {
        return invoiceId;
    }
    public Timestamp getDate() {
        return date;
    }
    public String getProduct() {
        return product;
    }
    public String getQuantity() {
        return quantity + "";
    }

    public void loadInvoiceData(String userId, final InvoiceDataCallback callback){
        db.collection("invoice")
                .whereEqualTo("userid",userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InvoiceModel> invoices = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            // Lấy dữ liệu từ mỗi hóa đơn và tạo đối tượng InvoiceModel
                            Long cost = documentSnapshot.getLong("cost");
                            Timestamp date = documentSnapshot.getTimestamp("date");
                            String invoiceId = documentSnapshot.getString("invoiceId");
                            String product = documentSnapshot.getString("product");
                            Number quantity = documentSnapshot.getDouble("quantity");

                            // Tạo đối tượng InvoiceModel và thêm vào danh sách
                            invoices.add(new InvoiceModel(cost, date, invoiceId, product, quantity));
                        }
                    }
                    callback.onSuccess(invoices);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }
    public interface InvoiceDataCallback {
        void onSuccess(List<InvoiceModel> invoiceModels);  // Callback khi lấy thành công
        void onFailure(Exception e);  // Callback khi có lỗi
    }
}
