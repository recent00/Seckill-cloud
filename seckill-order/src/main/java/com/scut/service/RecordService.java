package com.scut.service;

import com.scut.entity.Record;
import com.scut.exception.ServiceException;

public interface RecordService {
    /**
     * 新增订单
     * @param record
     * @return
     */
    int addRecord(Record record) throws ServiceException;

    /**
     * 根据id获取订单
     * @param id
     * @return
     */
    Record getRecordById(Integer id) throws ServiceException;

    /**
     * 获取秒杀结果
     * @param userId
     * @param productId
     * @return orderId:成功，-1：秒杀失败，0：排队中
     */
    int getResult(Integer userId, Integer productId);
}
