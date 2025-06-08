package com.example.uitpay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uitpay.R;
import com.example.uitpay.model.Invoice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {
    private List<Invoice> invoices = new ArrayList<>();
    private Context context;

    public InvoiceAdapter(Context context) {
        this.context = context;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices != null ? invoices : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoices.get(position);
        
        // Set invoice ID
        holder.invoiceIdText.setText(invoice.getInvoiceID() != null ? invoice.getInvoiceID() : "");
        
        // Set formatted date
        holder.invoiceDateText.setText(formatDate(invoice.getDate()));
        
        // Set formatted cost
        holder.invoiceCostText.setText(String.format(Locale.getDefault(), "%,.0f VND", invoice.getCost()));
        
        // Set description if available
        if (invoice.getDescription() != null && !invoice.getDescription().trim().isEmpty()) {
            holder.invoiceDescriptionText.setText(invoice.getDescription());
            holder.invoiceDescriptionText.setVisibility(View.VISIBLE);
        } else {
            holder.invoiceDescriptionText.setVisibility(View.GONE);
        }
        
        // Clear previous products and add current ones
        holder.productsContainer.removeAllViews();
        if (invoice.getProduct() != null) {
            for (Map.Entry<String, Invoice.InvoiceProduct> entry : invoice.getProduct().entrySet()) {
                addProductView(holder.productsContainer, entry.getValue());
            }
        }
    }

    private void addProductView(LinearLayout container, Invoice.InvoiceProduct product) {
        View productView = LayoutInflater.from(context).inflate(R.layout.item_invoice_product, container, false);
        
        ImageView productImageView = productView.findViewById(R.id.productImageView);
        TextView productNameText = productView.findViewById(R.id.productNameText);
        TextView productPriceText = productView.findViewById(R.id.productPriceText);
        TextView productQuantityText = productView.findViewById(R.id.productQuantityText);
        
        // Load product image
        if (product.getProductImage() != null && !product.getProductImage().isEmpty()) {
            Glide.with(context)
                .load(product.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.image_shimmer_effect)
                .error(R.drawable.image_shimmer_effect)
                .into(productImageView);
        } else {
            productImageView.setImageResource(R.drawable.image_shimmer_effect);
        }
        
        productNameText.setText(product.getName() != null ? product.getName() : "Sản phẩm");
        productPriceText.setText(String.format(Locale.getDefault(), "%,.0f VND", product.getPrice()));
        productQuantityText.setText(String.format(Locale.getDefault(), "x%d", product.getQuantity()));
        
        container.addView(productView);
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            // Parse Firebase date format: "July 5, 2025 at 7:02:27 AM UTC+7"
            SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a 'UTC'XXX", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (ParseException e) {
            // Fallback: try simpler format
            try {
                SimpleDateFormat fallbackFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                
                Date date = fallbackFormat.parse(dateString);
                return date != null ? outputFormat.format(date) : dateString;
            } catch (ParseException ex) {
                return dateString; // Return original if parsing fails
            }
        }
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView invoiceIdText;
        TextView invoiceDateText;
        TextView invoiceCostText;
        TextView invoiceDescriptionText;
        LinearLayout productsContainer;

        InvoiceViewHolder(View itemView) {
            super(itemView);
            invoiceIdText = itemView.findViewById(R.id.invoiceIdText);
            invoiceDateText = itemView.findViewById(R.id.invoiceDateText);
            invoiceCostText = itemView.findViewById(R.id.invoiceCostText);
            invoiceDescriptionText = itemView.findViewById(R.id.invoiceDescriptionText);
            productsContainer = itemView.findViewById(R.id.productsContainer);
        }
    }
} 