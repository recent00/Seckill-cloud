package com.scut.service.impl;

import cn.hutool.json.JSONUtil;
import com.scut.common.RespBean;
import com.scut.common.RespBeanEnum;
import com.scut.entity.Record;
import com.scut.exception.DAOException;
import com.scut.exception.ServiceException;
import com.scut.mapper.ProductMapper;
import com.scut.mapper.RecordMapper;
import com.scut.rabbitmq.MQSender;
import com.scut.service.SecKillService;
import com.scut.vo.BuyInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    MQSender mqSender;

    private ConcurrentHashMap<Integer, Boolean> EmptyStockMap = new ConcurrentHashMap<>();


    @Override
    public RespBean handleWithoutRedis(Integer userId, Integer productId, boolean optimisticLock) throws ServiceException {
        log.info("开始秒杀。。。。。。。。。");
        try {
            Record record = recordMapper.getRecordByUserIdAndProductId(userId, productId);
            if(record != null) {
                return RespBean.error(RespBeanEnum.REPEATE_ERROR);
            }
            Integer stock = productMapper.getStockById(productId);
            if(stock > 0){
                boolean update = updateAndInsertRecord(userId,productId,optimisticLock);
                if(update) return RespBean.success();
                else {
                    log.info("乐观加锁失败或库存不足，秒杀失败..userId: " + userId);
                    return RespBean.error(RespBeanEnum.EMPTY_STOCK);
                }
            }else {
                log.info("乐观加锁失败或库存不足，秒杀失败..userId: " + userId);
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
        } catch (DAOException e) {
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }

    @Override
    public RespBean handleByRedis(Integer userId, Integer productId, boolean optimisticLock) {
        log.info("开始秒杀。。。。。。。。。");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        try {
            //检查redis中的抢购名单是否包含该用户
            Record record = (Record)valueOperations.get("order:" + userId + ":" + productId);
            if(record != null) {
                return RespBean.error(RespBeanEnum.REPEATE_ERROR);
            }
            //内存标记，减少Redis的访问
            if (EmptyStockMap.get(productId) != null) {
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
            //判断库存数量
            Long stock = valueOperations.decrement("seckillProduct:" + productId);
            if(stock >= 0) {
                    // 乐观锁更新数据库中的库存数量并新增交易记录
                    boolean update = updateAndInsertRecord(userId, productId, optimisticLock);
                    // 若更新成功，则同时更新缓存
                    if(update) {
                        log.info("商品id：{} ,用户id：{} 秒杀成功,数据库库存更新：{}", productId, userId, stock);
                        valueOperations.set("order:" + userId + ":" + productId,new Record(null,userId,productId,1,null));
                        return RespBean.success();
                    }
                    valueOperations.increment("seckillProduct:" + productId);
                    log.info("乐观加锁失败或库存不足，秒杀失败..userId: " + userId);
                    return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }else {
                valueOperations.increment("seckillProduct:" + productId);
                EmptyStockMap.put(productId, true);
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
        } catch (ServiceException e) {
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }

    @Override
    public RespBean handleByRedisAndRabbitMQ(Integer userId, Integer productId, boolean optimisticLock) {
        log.info("开始秒杀。。。。。。。。。");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //检查redis中的抢购名单是否包含该用户
        Record record = (Record)valueOperations.get("order:" + userId + ":" + productId);
        if(record != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记，减少Redis的访问
        if (EmptyStockMap.get(productId) != null) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断库存数量
        Long stock = valueOperations.decrement("seckillProduct:" + productId);
        if(stock >= 0) {
            //发送消息给消息队列异步处理订单
            SendMessage(userId, productId);
            return RespBean.success(0);
        }else {
            valueOperations.increment("seckillProduct:" + productId);
            EmptyStockMap.put(productId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
    }

    /**
     * 一个事务内更新库存并生成订单
     * @param userId 用户ID
     * @param productId 商品ID
     * @param optimisticLock 是否采用乐观锁
     * @return
     * @throws ServiceException
     */
    @Override
    @Transactional
    public boolean updateAndInsertRecord(Integer userId, Integer productId, boolean optimisticLock) throws ServiceException {
        int update = 0;
        if(userId == null) throw new ServiceException("userId不能为空");
        if(productId == null) throw new ServiceException("productId不能为空");
        try {
            if(optimisticLock) {
                Integer versionId = productMapper.getVersionId(productId);
                log.info("版本号：{}",versionId);
                update = productMapper.updateStockByLock(productId,versionId);
                log.info("update：{}",update);
            }else {
                update = productMapper.updateProductStock(productId);
            }
            if(update == 1) {
                Record record = new Record(null,userId,productId,1,new Date());
                recordMapper.addRecord(record);
            }
        }catch (Exception d) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//如果你try()catch()了，框架默认你手动处理了异常于是不回滚，因此需要加入此句告诉框架需要回滚
            if(d instanceof DuplicateKeyException)//加了唯一索引，插入重复后报的异常
                throw new ServiceException("该商品每人限购一件");
            else throw new ServiceException(d.getMessage());
        }
        return update == 1;
    }

    /**
     * 发送订单消息给rabbitmq
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public void SendMessage(Integer userId, Integer productId) {
        BuyInformation buyInformation = new BuyInformation(userId,productId);
        mqSender.sendSeckillMessage(JSONUtil.toJsonStr(buyInformation));
    }
}
