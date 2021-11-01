package com.scut.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.scut.exception.ServiceException;
import com.scut.service.SecKillService;
import com.scut.vo.BuyInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    SecKillService secKillService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        BuyInformation buyInformation = JSONUtil.toBean(message, BuyInformation.class);
        try {
            secKillService.ReceiveAndUpdate(buyInformation.getUserId(),buyInformation.getProductId(),false);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
