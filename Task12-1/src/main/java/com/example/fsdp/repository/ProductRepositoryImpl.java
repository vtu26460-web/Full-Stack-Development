package com.example.fsdp.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.fsdp.entity.Product;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private List<Product> productList = new ArrayList<>();

    @Override
    public Product save(Product product) {
        productList.add(product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return productList;
    }

    @Override
    public Product findById(int id) {
        for (Product p : productList) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Product update(int id, Product product) {
        for (Product p : productList) {
            if (p.getId() == id) {
                p.setName(product.getName());
                p.setCategory(product.getCategory());
                p.setPrice(product.getPrice());
                p.setQuantity(product.getQuantity());
                return p;
            }
        }
        return null;
    }

    @Override
    public void delete(int id) {
        productList.removeIf(p -> p.getId() == id);
    }

    @Override
    public void deleteAll() {
        productList.clear();
    }
}