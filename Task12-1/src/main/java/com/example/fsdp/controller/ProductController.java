package com.example.fsdp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.fsdp.entity.Product;
import com.example.fsdp.repository.ProductRepository;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository repo;

    // CREATE
    @PostMapping("/create")
    public Product create(@RequestBody Product product) {
        return repo.save(product);
    }

    // READ (All)
    @GetMapping("/read")
    public List<Product> readAll() {
        return repo.findAll();
    }

    // READ (By ID)
    @GetMapping("/read/{id}")
    public Product readById(@PathVariable int id) {
        return repo.findById(id);
    }

    // UPDATE (Without ID)
    @PutMapping("/update")
    public Product update(@RequestBody Product product) {
        return repo.update(product.getId(), product);
    }

    // UPDATE (With ID)
    @PutMapping("/update/{id}")
    public Product updateById(@PathVariable int id,
                              @RequestBody Product product) {
        return repo.update(id, product);
    }

    // DELETE (All)
    @DeleteMapping("/delete")
    public String deleteAll() {
        repo.deleteAll();
        return "All products deleted";
    }

    // DELETE (By ID)
    @DeleteMapping("/delete/{id}")
    public String deleteById(@PathVariable int id) {
        repo.delete(id);
        return "Product deleted with ID: " + id;
    }
}