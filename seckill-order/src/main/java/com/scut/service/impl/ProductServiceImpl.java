package com.scut.service.impl;

import com.scut.entity.Product;
import com.scut.mapper.ProductMapper;
import com.scut.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Override
    public List<Product> getAllProduct() {
        return productMapper.getAllProduct();
    }
}
