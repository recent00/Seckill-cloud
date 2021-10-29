package com.scut.controller;

import com.scut.common.RespBean;
import com.scut.entity.Product;
import com.scut.exception.ServiceException;
import com.scut.service.ProductService;
import com.scut.service.RecordService;
import com.scut.service.SecKillService;
import com.scut.vo.BuyInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seckill")
@Slf4j
public class SecKillController implements InitializingBean {

    @Autowired
    private SecKillService secKillService;
    @Autowired
    private ProductService productService;
    @Autowired
    private RecordService recordService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 数据库普通模式
     * 秒杀商品数1000
     * 并发量1000，循环10次
     * 吞吐量：326.7/sec
     * @param buyInformation
     * @return
     * @throws Exception
     */
    @GetMapping("/1")
    public RespBean test1(BuyInformation buyInformation) throws Exception {
        System.out.println(buyInformation);
        return secKillService.handleWithoutRedis(buyInformation.getUserId(),buyInformation.getProductId(),false);
    }

    /**
     * 数据库乐观锁模式
     * 秒杀商品数1000
     * 并发量1000，循环10次
     * 吞吐量：406.3/sec
     * @param buyInformation
     * @return
     * @throws Exception
     */
    @GetMapping("/2")
    public RespBean test2(BuyInformation buyInformation) throws Exception {
        log.info("测试2 --- userId: {} ----- priductId: {}",buyInformation.getUserId(),buyInformation.getProductId());
        return secKillService.handleWithoutRedis(buyInformation.getUserId(),buyInformation.getProductId(),true);
    }

    /**
     * redis+数据库普通模式
     * 秒杀商品数1000
     * 并发量1000，循环10次
     * 吞吐量：456.6/sec
     * @param buyInformation
     * @return
     * @throws Exception
     */
    @GetMapping("/3")
    public RespBean test3(BuyInformation buyInformation) throws Exception {
        log.info("测试3 --- userId: {} ----- priductId: {}",buyInformation.getUserId(),buyInformation.getProductId());
        return secKillService.handleByRedis(buyInformation.getUserId(),buyInformation.getProductId(),false);
    }

    /**
     * redis+数据库乐观锁模式
     * 秒杀商品数1000
     * 并发量1000，循环10次
     * 吞吐量：469.0/sec
     * 出现问题：库存遗留：分析：加乐观锁会出现库存遗留的原因是因为许多线程在判断完库存后通过版本号判断时失败，导致未能秒杀成功
     * 对比不使用redis时加乐观锁出现库存遗留的情况很少的原因：通过访问redis的速度远快于数据库，因此判断库存的操作时用redis会使很多线程判断成功，然后同时判断版本号
     * 造成很多线程由于版本号不一致而秒杀失败
     * @param buyInformation
     * @return
     * @throws Exception
     */
    @GetMapping("/4")
    public RespBean test4(BuyInformation buyInformation) throws Exception {
        log.info("测试4 --- userId: {} ----- priductId: {}",buyInformation.getUserId(),buyInformation.getProductId());
        return secKillService.handleByRedis(buyInformation.getUserId(),buyInformation.getProductId(),true);
    }

    /**
     * redis+rabbitmq
     * 秒杀商品数1000
     * 并发量1000，循环10次
     * 吞吐量：1201.2/sec
     * @param buyInformation
     * @return
     * @throws Exception
     */
    @GetMapping("/5")
    public RespBean test5(BuyInformation buyInformation) throws Exception {
        log.info("测试5 --- userId: {} ----- priductId: {}",buyInformation.getUserId(),buyInformation.getProductId());
        return secKillService.handleByRedisAndRabbitMQ(buyInformation.getUserId(),buyInformation.getProductId(),false);
    }

    @GetMapping("/result")
    public RespBean getResult(BuyInformation buyInformation) {
        int result = recordService.getResult(buyInformation.getUserId(), buyInformation.getProductId());
        return RespBean.success(result);
    }

    /**
     * 系统初始化，把商品库存数量加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<Product> list = productService.getAllProduct();
        if(list.size() == 0) return;
        for (Product product : list) {
            redisTemplate.opsForValue().set("seckillProduct:" + product.getId(),product.getStock());
        }
    }
}
