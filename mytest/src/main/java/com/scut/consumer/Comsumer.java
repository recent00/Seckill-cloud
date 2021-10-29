package com.scut.consumer;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.scut.entity.ParseData;
import com.scut.entity.canalTbl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
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
        ParseData<canalTbl> data = JSONUtil.toBean(msg, new TypeReference<ParseData<canalTbl>>() {
        }, true);

        List<canalTbl> list = data.getData();

        HashOperations hops = redisTemplate.opsForHash();
        if("DELETE".equals(data.getType())){//删除
            hops.delete("hot:key",list.get(0).getId()+"");
        }else{//增改
            hops.put("hot:key",list.get(0).getId()+"",list.get(0)+"");
        }
    }
}
