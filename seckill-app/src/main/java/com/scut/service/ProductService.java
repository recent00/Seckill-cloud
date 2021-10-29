package com.scut.service;

import com.scut.entity.Product;
import com.scut.exception.DAOException;
import com.scut.exception.ServiceException;

import java.util.List;

public interface ProductService {
    /**
     * 查找所有的秒杀商品
     * @return
     * @throws DAOException
     */
    List<Product> getAllProduct();
}
