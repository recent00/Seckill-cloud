package com.scut.mapper;

import com.scut.entity.Record;
import com.scut.exception.DAOException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecordMapper {

    /**
     * 新增订单
     * @param record
     * @return
     */
    int addRecord(Record record) throws DAOException;

    /**
     * 根据id获取订单
     * @param id
     * @return
     */
    Record getRecordById(Integer id) throws DAOException;

    /**
     * 根据用户id和商品id查询订单
     * @param userId
     * @param productId
     * @return
     */
    Record getRecordByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);
}
