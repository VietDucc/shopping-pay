package com.example.uitpay.ui.notifications;

import android.content.Context;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProductCsvReader {
    
    private final Context context;
    
    public ProductCsvReader(Context context) {
        this.context = context;
    }
    
    public List<Product> readProductsFromCsv() {
        List<Product> products = new ArrayList<>();
        
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    context.getAssets().open("products.csv"));
            
            CSVReader csvReader = new CSVReader(inputStreamReader);
            String[] nextLine;
            
            // Skip the header line
            csvReader.readNext();
            
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length >= 5) {
                    Product product = new Product(
                            Integer.parseInt(nextLine[0]),
                            nextLine[1],
                            Double.parseDouble(nextLine[2]),
                            nextLine[3],
                            nextLine[4]
                    );
                    products.add(product);
                }
            }
            
            csvReader.close();
            
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        
        return products;
    }
    
    public String getProductsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Product> products = readProductsFromCsv();
        
        stringBuilder.append("Danh sách sản phẩm và vị trí:\n");
        
        for (Product product : products) {
            stringBuilder.append("- ID: ").append(product.getId())
                    .append(", Tên: ").append(product.getName())
                    .append(", Giá: ").append(String.format("%,d", (int)product.getPrice())).append(" VND")
                    .append(", Vị trí: ").append(product.getLocation())
                    .append("\n");
        }
        
        return stringBuilder.toString();
    }
} 