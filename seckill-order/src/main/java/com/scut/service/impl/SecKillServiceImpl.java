package com.scut.service.impl;

import cn.hutool.json.JSONUtil;
import com.scut.common.RespBean;
import com.scut.common.RespBeanEnum;
import com.scut.entity.Record;
import com.scut.exception.DAOException;
import com.scut.exception.ServiceException;
import com.scut.mapper.ProductMapper;
import com.scut.mapper.RecordMapper;
import com.scut.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;

@Slf4j
@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private RedisTemplate redisTemplate;


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
     * 接收消息队列中的消息并更新
     * @param userId
     * @param productId
     * @param optimisticLock
     * @throws ServiceException
     */
    @Override
    public void ReceiveAndUpdate(Integer userId, Integer productId, boolean optimisticLock) throws ServiceException {
        try{
            Integer stock = productMapper.getStockById(productId);
            if(stock > 0){
                boolean update = updateAndInsertRecord(userId,productId,optimisticLock);
                if(update) {
                    log.info("商品id：{} ,用户id：{} 秒杀成功,数据库库存更新：{}", productId, userId, stock);
                    redisTemplate.opsForValue().set("order:" + userId + ":" + productId,new Record(null,userId,productId,1,null));
                    return;
                }
                else {
                    redisTemplate.opsForValue().increment("seckillProduct:" + productId);
                    log.info("乐观加锁失败或库存不足，秒杀失败..userId: " + userId);
                    return;
                }
            }else {
                redisTemplate.opsForValue().set("isStockEmpty:" + productId,"0");
                log.info("乐观加锁失败或库存不足，秒杀失败..userId: " + userId);
                return;
            }
        }catch (Exception e) {
            redisTemplate.opsForValue().increment("seckillProduct:" + productId);
            return;
        }
    }
}
