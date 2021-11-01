package com.scut.mapper;

import com.scut.entity.Product;
import com.scut.exception.DAOException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    /**
     * 增加秒杀商品
     * @param product
     * @return
     */
    int addProduct(Product product) throws DAOException;

    /**
     * 根据id查找商品
     * @param id
     * @return
     */
    Product getProductById(@Param("id") Integer id) throws DAOException;

    /**
     * 查找所有的秒杀商品
     * @return
     * @throws DAOException
     */
    List<Product> getAllProduct();

    /**
     * 更新库存
     * @param id
     * @return
     */
    int updateProductStock(Integer id);

    /**
     * 根据id查找库存
     * @param id
     * @return
     */
    Integer getStockById(Integer id) throws DAOException;

    /**
     * 根据id获取版本号
     * @param id
     * @return
     * @throws DAOException
     */
    Integer getVersionId(Integer id) throws DAOException;

    /**
     * 利用乐观锁更新商品库存
     * @param id
     * @param versionId
     * @return
     * @throws DAOException
     */
    int updateStockByLock(@Param("id") Integer id, @Param("versionId") Integer versionId) throws DAOException;
}
