package com.example.fsdp.repository;

import java.util.List;
import com.example.fsdp.entity.Product;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll();

    Product findById(int id);

    Product update(int id, Product product);

    void delete(int id);

    void deleteAll();
}