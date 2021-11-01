package com.scut.consumer;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.scut.entity.Product;
import com.scut.vo.ParseData;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Comsumer {

    @Autowired
    RedisTemplate redisTemplate;

    @RabbitListener(queues = "example2-queue")
    public void receiveConfirmQueue(Message message){
        String msg = new String(message.getBody());
        System.out.println("收到信息：" + msg);
        //序列化成javabean
        ParseData<Product> data = JSONUtil.toBean(msg, new TypeReference<ParseData<Product>>() {
        }, true);

        List<Product> list = data.getData();
        if("seckill".equals(data.getDatabase()) && "tb_product".equals(data.getTable())) {
            ValueOperations ops = redisTemplate.opsForValue();
            Product product = list.get(0);
            int productId = product.getId();
            int stock = product.getStock();
            if("UPDATE".equals(data.getType())){
                ops.set("seckillProduct:" + productId,stock);
            }
        }
    }
}
