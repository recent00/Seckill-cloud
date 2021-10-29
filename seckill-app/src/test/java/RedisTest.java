import com.scut.Application;
import com.scut.common.RespBean;
import com.scut.common.RespBeanEnum;
import com.scut.entity.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void RedisTest() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //检查redis中的抢购名单是否包含该用户
        Record record = (Record)valueOperations.get("order:" + 1 + ":" + 1);
        if(record != null) {
            System.out.println("失败");
        }
        //判断库存数量
        Integer stock = (Integer)valueOperations.get("seckillProduct:1");
        System.out.println(stock);
    }
}
