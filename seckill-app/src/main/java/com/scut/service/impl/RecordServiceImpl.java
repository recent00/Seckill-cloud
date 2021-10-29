package com.scut.service.impl;

import com.scut.entity.Record;
import com.scut.exception.DAOException;
import com.scut.exception.ServiceException;
import com.scut.mapper.RecordMapper;
import com.scut.service.RecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    RecordMapper recordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增订单
     * @param record
     * @return
     */
    @Override
    public int addRecord(Record record) throws ServiceException {
        if (record.getUserId() == null) {
            throw new ServiceException("userId不能为空");
        }
        if (record.getProductId() == null) {
            throw new ServiceException("productId不能为空");
        }
       try {
           return recordMapper.addRecord(record);
       }catch (DAOException d){
           throw new ServiceException("操作失败");
       }
    }

    /**
     * 根据id获取订单
     * @param id
     * @return
     */
    @Override
    public Record getRecordById(Integer id) throws ServiceException {
        if(id == null) {
            throw new ServiceException("id不能为空");
        }
        try {
            return recordMapper.getRecordById(id);
        } catch (DAOException d) {
            throw new ServiceException("操作失败");
        }
    }

    /**
     * 客户端查看排队结果
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public int getResult(Integer userId, Integer productId) {
        Record record = recordMapper.getRecordByUserIdAndProductId(userId, productId);
        if(record != null) return record.getId();
        else if(redisTemplate.hasKey("isStockEmpty:" + productId)) return -1;
        else return 0;
    }
}
