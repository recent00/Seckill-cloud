package com.scut.service;

import com.scut.common.RespBean;
import com.scut.exception.ServiceException;

public interface SecKillService {


    /**
     * 不通过缓存，直接进行秒杀，秒杀成功同时直接新增交易记录
     * @param userId 用户ID
     * @param productId 商品ID
     * @param optimisticLock 是否采用乐观锁
     * @return
     */
    RespBean handleWithoutRedis(Integer userId,Integer productId,boolean optimisticLock) throws ServiceException;

    /**
     * 通过缓存预查询，再进行秒杀，秒杀成功同时新增交易记录
     * @param userId 用户ID
     * @param productId 商品ID
     * @param optimisticLock 是否采用乐观锁
     * @return
     */
    RespBean handleByRedis(Integer userId,Integer productId,boolean optimisticLock) throws ServiceException;

    /**
     * 通过缓存预查询进行秒杀，交易记录通过RabbitMQ异步处理
     * @param userId 用户ID
     * @param productId 商品ID
     * @param optimisticLock 是否采用乐观锁
     * @return
     */
    RespBean handleByRedisAndRabbitMQ(Integer userId,Integer productId,boolean optimisticLock) throws ServiceException;

    /**
     * 在一个事务中，更新库存并新增交易记录
     * @param userId 用户ID
     * @param productId 商品ID
     * @param optimisticLock 是否采用乐观锁
     * @return
     */
    boolean updateAndInsertRecord(Integer userId,Integer productId,boolean optimisticLock) throws ServiceException;

    /**
     * 发送订单消息给rabbitmq
     * @param userId
     * @param productId
     * @return
     */
    void SendMessage(Integer userId, Integer productId);
}
