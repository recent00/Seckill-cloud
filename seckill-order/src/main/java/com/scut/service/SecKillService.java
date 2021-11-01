package com.scut.service;

import com.scut.common.RespBean;
import com.scut.exception.ServiceException;

public interface SecKillService {

    /**
     * 在一个事务中，更新库存并新增交易记录
     * @param userId 用户ID
     * @param productId 商品ID
     * @param optimisticLock 是否采用乐观锁
     * @return
     */
    boolean updateAndInsertRecord(Integer userId, Integer productId, boolean optimisticLock) throws ServiceException;


    /**
     * 接收消息队列中的消息并更新
     * @param userId
     * @param productId
     * @param optimisticLock
     * @return
     * @throws ServiceException
     */
    void ReceiveAndUpdate(Integer userId, Integer productId, boolean optimisticLock) throws ServiceException;
}
