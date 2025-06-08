package com.example.uitpay.ui.invoice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpay.R;
import com.example.uitpay.adapter.InvoiceAdapter;
import com.example.uitpay.model.Invoice;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.Timestamp;

import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvoiceListActivity extends AppCompatActivity {
    private static final String TAG = "InvoiceListActivity";
    
    private RecyclerView invoiceRecyclerView;
    private ProgressBar loadingProgressBar;
    private LinearLayout emptyStateLayout;
    private TextView invoiceCountText;
    private ImageButton backButton;
    
    private InvoiceAdapter invoiceAdapter;
    private FirebaseFirestore db;
    private List<Invoice> invoiceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        initViews();
        setupRecyclerView();
        setupFirebase();
        loadInvoices();
    }

    private void initViews() {
        invoiceRecyclerView = findViewById(R.id.invoiceRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        invoiceCountText = findViewById(R.id.invoiceCountText);
        backButton = findViewById(R.id.backButton);

        // Setup back button
        backButton.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        invoiceAdapter = new InvoiceAdapter(this);
        invoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        invoiceRecyclerView.setAdapter(invoiceAdapter);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore instance initialized");
        Log.d(TAG, "Firestore app: " + db.getApp().getName());
    }

    private void loadInvoices() {
        showLoading(true);
        Log.d(TAG, "=== Starting loadInvoices ===");
        
        db.collection("invoice")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "=== Firestore SUCCESS ===");
                    invoiceList.clear();
                    
                    Log.d(TAG, "Firestore data received");
                    Log.d(TAG, "Total invoice documents: " + queryDocumentSnapshots.size());
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "No invoices found in Firestore");
                        updateUI();
                        return;
                    }
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String invoiceId = document.getId();
                            Log.d(TAG, "Processing invoice: " + invoiceId);
                            Log.d(TAG, "Document data: " + document.getData());
                            
                            Invoice invoice = new Invoice();
                            invoice.setInvoiceID(invoiceId);
                            
                            // Parse các field từ Firestore
                            if (document.contains("cost")) {
                                Object costValue = document.get("cost");
                                if (costValue instanceof Number) {
                                    invoice.setCost(((Number) costValue).doubleValue());
                                } else if (costValue instanceof String) {
                                    try {
                                        invoice.setCost(Double.parseDouble((String) costValue));
                                    } catch (NumberFormatException e) {
                                        invoice.setCost(0);
                                    }
                                }
                            }
                            
                            if (document.contains("date")) {
                                Object dateValue = document.get("date");
                                if (dateValue instanceof com.google.firebase.Timestamp) {
                                    com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) dateValue;
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                                    invoice.setDate(sdf.format(timestamp.toDate()));
                                    Log.d(TAG, "Parsed timestamp to date: " + invoice.getDate());
                                } else if (dateValue instanceof String) {
                                    invoice.setDate((String) dateValue);
                                }
                            }
                            
                            if (document.contains("description")) {
                                invoice.setDescription(document.getString("description"));
                            }
                            
                            if (document.contains("invoiceID")) {
                                invoice.setInvoiceID(document.getString("invoiceID"));
                            }
                            
                            if (document.contains("userid")) {
                                invoice.setUserid(document.getString("userid"));
                            }
                            
                            // Parse products map
                            if (document.contains("product")) {
                                Object productObj = document.get("product");
                                Log.d(TAG, "Product object type: " + (productObj != null ? productObj.getClass().getSimpleName() : "null"));
                                Log.d(TAG, "Product object value: " + productObj);
                                
                                if (productObj instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> productMap = (Map<String, Object>) productObj;
                                    Log.d(TAG, "Product map size: " + productMap.size());
                                    Log.d(TAG, "Product map keys: " + productMap.keySet());
                                    loadInvoiceProductsFromMap(invoice, productMap);
                                } else {
                                    Log.w(TAG, "Product is not a Map, adding invoice without products");
                                    invoiceList.add(invoice);
                                    updateUI();
                                }
                            } else {
                                Log.w(TAG, "No product field found in invoice");
                                invoiceList.add(invoice);
                                updateUI();
                            }
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing invoice: " + e.getMessage(), e);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "=== Firestore FAILED ===");
                    Log.e(TAG, "Firestore error: " + e.getMessage(), e);
                    showLoading(false);
                    showEmptyState(true);
                });
    }

    private void loadInvoiceProductsFromMap(Invoice invoice, Map<String, Object> firestoreProductMap) {
        Map<String, Invoice.InvoiceProduct> productMap = new java.util.HashMap<>();
        final int[] pendingRequests = {firestoreProductMap.size()};
        
        Log.d(TAG, "Loading " + pendingRequests[0] + " products for invoice: " + invoice.getInvoiceID());
        
        if (pendingRequests[0] == 0) {
            invoice.setProduct(productMap);
            invoiceList.add(invoice);
            updateUI();
            return;
        }
        
        for (Map.Entry<String, Object> entry : firestoreProductMap.entrySet()) {
            String productId = entry.getKey();
            Object productValue = entry.getValue();
            
            Log.d(TAG, "Processing product: " + productId);
            Log.d(TAG, "Product data: " + productValue);
            
            Invoice.InvoiceProduct invoiceProduct = new Invoice.InvoiceProduct();
            
            if (productValue instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> productData = (Map<String, Object>) productValue;
                
                // Lấy thông tin từ invoice
                if (productData.containsKey("name")) {
                    invoiceProduct.setName((String) productData.get("name"));
                }
                
                if (productData.containsKey("price")) {
                    Object priceValue = productData.get("price");
                    if (priceValue instanceof Number) {
                        invoiceProduct.setPrice(((Number) priceValue).doubleValue());
                    } else if (priceValue instanceof String) {
                        try {
                            invoiceProduct.setPrice(Double.parseDouble((String) priceValue));
                        } catch (NumberFormatException e) {
                            invoiceProduct.setPrice(0);
                        }
                    }
                }
                
                if (productData.containsKey("quantity")) {
                    Object quantityValue = productData.get("quantity");
                    if (quantityValue instanceof Number) {
                        invoiceProduct.setQuantity(((Number) quantityValue).intValue());
                    } else if (quantityValue instanceof String) {
                        try {
                            invoiceProduct.setQuantity(Integer.parseInt((String) quantityValue));
                        } catch (NumberFormatException e) {
                            invoiceProduct.setQuantity(0);
                        }
                    }
                }
            }
            
            // Truy vấn thông tin chi tiết từ collection product
            Log.d(TAG, "Querying product collection for: " + productId);
            db.collection("product").document(productId)
                    .get()
                    .addOnSuccessListener(productDocument -> {
                        Log.d(TAG, "Product query success for: " + productId + ", exists: " + productDocument.exists());
                        try {
                            if (productDocument.exists()) {
                                if (productDocument.contains("description")) {
                                    invoiceProduct.setDescription(productDocument.getString("description"));
                                }
                                
                                if (productDocument.contains("productImage")) {
                                    invoiceProduct.setProductImage(productDocument.getString("productImage"));
                                }
                                
                                // Cập nhật name và price từ collection product nếu có
                                if (productDocument.contains("name")) {
                                    String productName = productDocument.getString("name");
                                    if (productName != null && !productName.isEmpty()) {
                                        invoiceProduct.setName(productName);
                                    }
                                }
                                
                                Log.d(TAG, "Loaded product details: " + invoiceProduct.getName() + " - " + invoiceProduct.getDescription());
                            } else {
                                Log.w(TAG, "Product not found in product collection: " + productId);
                            }
                            
                            productMap.put(productId, invoiceProduct);
                            
                            // Giảm số request còn lại
                            pendingRequests[0]--;
                            Log.d(TAG, "Remaining requests: " + pendingRequests[0]);
                            
                            // Nếu đã load xong tất cả sản phẩm
                            if (pendingRequests[0] == 0) {
                                invoice.setProduct(productMap);
                                invoiceList.add(invoice);
                                Log.d(TAG, "Added complete invoice: " + invoice.getInvoiceID() + " with " + productMap.size() + " products");
                                updateUI();
                            }
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading product details: " + e.getMessage(), e);
                            productMap.put(productId, invoiceProduct);
                            pendingRequests[0]--;
                            
                            if (pendingRequests[0] == 0) {
                                invoice.setProduct(productMap);
                                invoiceList.add(invoice);
                                updateUI();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading product: " + e.getMessage());
                        productMap.put(productId, invoiceProduct);
                        pendingRequests[0]--;
                        
                        if (pendingRequests[0] == 0) {
                            invoice.setProduct(productMap);
                            invoiceList.add(invoice);
                            updateUI();
                        }
                    });
        }
    }

    private void updateUI() {
        Log.d(TAG, "=== updateUI called ===");
        Log.d(TAG, "Invoice list size: " + invoiceList.size());
        
        showLoading(false);
        
        if (invoiceList.isEmpty()) {
            Log.d(TAG, "Showing empty state");
            showEmptyState(true);
            invoiceCountText.setText("0 hóa đơn");
        } else {
            Log.d(TAG, "Showing " + invoiceList.size() + " invoices");
            showEmptyState(false);
            invoiceCountText.setText(invoiceList.size() + " hóa đơn");
            invoiceAdapter.setInvoices(invoiceList);
        }
        
        Log.d(TAG, "UI updated with " + invoiceList.size() + " invoices");
    }

    private void showLoading(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        invoiceRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        invoiceRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
} 