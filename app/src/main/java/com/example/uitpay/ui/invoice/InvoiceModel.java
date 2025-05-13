package com.example.uitpay.ui.invoice;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class InvoiceModel {
    private Long cost;
    private Timestamp date;
    private String invoiceId;
    private Map<String, Map<String, Object>> product;

    private FirebaseFirestore db;

    public InvoiceModel() {
        db = FirebaseFirestore.getInstance();
    }

    public InvoiceModel(Long cost, Timestamp date, String invoiceId, Map<String, Map<String, Object>> product) {
        this.cost = cost;
        this.date = date;
        this.invoiceId = invoiceId;
        this.product = product;
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
    public Map<String, Map<String, Object>> getProduct() {
        return product;
    }

    public List<String> getProductKeys() {
        return new ArrayList<>(product.keySet());
    }
    public String getProductName(String productKey) {
        Map<String, Object> productDetails = product.get(productKey);
        return (String) productDetails.get("name");
    }
    public String getProductPrice(String productKey) {
        Map<String, Object> productDetails = product.get(productKey);
        return ": " + productDetails.get("price") + " đ";
    }

    public String getProductQuantity(String productKey) {
        Map<String, Object> productDetails = product.get(productKey);
        return "x"+ productDetails.get("quantity");
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
                            String invoiceId = documentSnapshot.getString("invoiceID");
                            Map<String, Map<String, Object>> product = (Map<String, Map<String, Object>>) documentSnapshot.get("product");
                            // Tạo đối tượng InvoiceModel và thêm vào danh sách
                            invoices.add(new InvoiceModel(cost, date, invoiceId, product));
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
