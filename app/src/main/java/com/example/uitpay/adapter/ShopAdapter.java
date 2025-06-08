package com.example.uitpay.adapter;

import android.content.Intent;
import android.net.Uri;
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
import com.example.uitpay.model.Shop;
import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private List<Shop> shops;

    public ShopAdapter() {
        this.shops = new ArrayList<>();
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops != null ? shops : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shops.get(position);
        holder.bind(shop);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageShop;
        private TextView textShopName;
        private TextView textShopAddress;
        private LinearLayout layoutMapButton;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            imageShop = itemView.findViewById(R.id.imageShop);
            textShopName = itemView.findViewById(R.id.textShopName);
            textShopAddress = itemView.findViewById(R.id.textShopAddress);
            layoutMapButton = itemView.findViewById(R.id.layoutMapButton);
        }

        public void bind(Shop shop) {
            // Set tên cửa hàng
            textShopName.setText(shop.getName());
            
            // Set địa chỉ
            textShopAddress.setText(shop.getAddress());

            // Load hình ảnh cửa hàng
            if (shop.getImageUrl() != null && !shop.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(shop.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_shop_placeholder)
                    .error(R.drawable.ic_shop_placeholder)
                    .into(imageShop);
            } else {
                imageShop.setImageResource(R.drawable.ic_shop_placeholder);
            }

            // Xử lý click vào nút bản đồ
            layoutMapButton.setOnClickListener(v -> {
                if (shop.getMapUrl() != null && !shop.getMapUrl().isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shop.getMapUrl()));
                        intent.setPackage("com.google.android.apps.maps");
                        
                        // Kiểm tra xem có Google Maps không
                        if (intent.resolveActivity(itemView.getContext().getPackageManager()) != null) {
                            itemView.getContext().startActivity(intent);
                        } else {
                            // Nếu không có Google Maps, mở bằng trình duyệt web
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shop.getMapUrl()));
                            itemView.getContext().startActivity(webIntent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
} 