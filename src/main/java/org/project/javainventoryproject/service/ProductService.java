package org.project.javainventoryproject.service;



import org.project.javainventoryproject.entity.Product;
import org.project.javainventoryproject.exception.SkuAlreadyExistsException;
import org.project.javainventoryproject.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository repository){
        this.repository = repository;
    }

    public Product create(Product product){
        repository.findBySku(product.getSku()).ifPresent(p -> {
            throw new SkuAlreadyExistsException("The SKU '" + product.getSku() + "' already exists.");
        });
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return repository.save(product);
    }
}
