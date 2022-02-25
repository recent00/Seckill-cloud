# SecKill-System

本项目是基于Spring cloud Alibaba的高并发秒杀系统，用于个人学习。

### 项目结构

- 服务注册与发现中心：**Nacos**
- API网关：**Gateway**
- 缓存：**Redis**
- 消息队列：**RabbitMq**
- 数据库：**MySQl**
- 熔断与限流：**Sentinel**




### 功能架构

- 秒杀微服务
- 订单微服务
- canal监听微服务



#### 秒杀微服务：

1. 实现秒杀基础功能（对比几种加锁方式，解决超卖，库存余留等问题）
2. 秒杀成功信息添加消息队列，异步生成订单



#### 订单微服务：

监听消息队列，生成订单进数据库



#### canal监听微服务：

通过对数据库进行binlog监听，更新redis



### 项目部署

**MySQL**：docker部署

docker run -p 3306:3306 --name mysql1 -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.5

开启binlog日志：

`docker exec 容器id bash -c "echo 'log-bin=/var/lib/mysql/mysql-bin' >> /etc/mysql/my.cnf"`

`docker exec 容器id bash -c "echo 'server-id=123454' >> /etc/mysql/my.cnf"`

`docker restart 容器id`

**redis：**docker部署

```
docker run -p 6379:6379 -v /lin/myredis/data:/data -v /lin/myredis/conf/redis.conf:/usr/local/etc/redis/redis.conf  -d redis redis-server /usr/local/etc/redis/redis.conf --appendonly yes
```

 docker exec -it 运行着Rediis服务的容器ID redis-cli 进入客户端



**rabbitmq：**docker部署

`docker run -d -p 5672:5672 -p 15672:15672 rabbitmq镜像id`



**canal**：centos6.8直接下载canal

参照：https://www.jianshu.com/p/60a9176a8825

docker部署：参照https://www.siques.cn/doc/340，不知为啥用docker时，消息队列收不到消息



### 数据库结构

数据库分为用户表，秒杀商品表和订单表

#### SQL

tb_user：

```sql
CREATE TABLE tb_user(
    `id` int(32) not null AUTO_INCREMENT comment 'ID',
    `user_name` varchar (50) not null comment '用户名',
    `phone` varchar (20) not null comment '手机号码',
    `create_time` datetime not null default now() NOT NULL comment "创建时间",
     primary key (`id`)

) ENGINE=InnoDB default charset='utf8';

```



tb_product：

```sql
CREATE TABLE tb_product (
  id int(32) not null AUTO_INCREMENT COMMENT 'ID',
  product_name varchar(50) not null COMMENT '产品名称',
  price decimal(16,3) not null COMMENT '价格',
  stock int(64) not null COMMENT '库存',
  version_id int(128) default '0' COMMENT '版本id',
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

```



tb_record:

```sql
CREATE TABLE tb_record(
    `id` int(64) not null AUTO_INCREMENT comment 'ID',
    `user_id` int(32) not null comment '用户ID',
    `product_id` int(32) not null comment '产品ID',
    `state` tinyint(3) not null comment '秒杀状态: 1秒杀成功,0秒杀失败,-1重复秒杀,-2系统异常',
    `create_time` datetime not null default now() comment '创建时间',
    primary key (`id`),
    UNIQUE  INDEX key_user_id_product_id (`user_id`,`product_id`)
)ENGINE=InnoDB default charset='utf8';

```

key_user_id_product_id：是唯一索引，防止用户重复抢购商品。



### 秒杀核心思路

![秒杀核心思路](D:\lys\java\project\Seckill-cloud\秒杀核心思路.png)

### 技术要点

- #### **缓存查询**

  预加载商品库存到Redis缓存，提高查询速率。

  使用ConcurrentHashMap进行内存标记，减少Redis的访问，提高查询效率


- #### 异步下单

  Redis减库存后，通过Rabbitmq进行异步下单，更新数据库；

  通过订单微服务监听消息队列，进行订单生成与更新数据库。

- #### 限流

  利用spring cloud Alibaba Sentinal进行流量控制，进行接口防刷，减小服务器压力：

  ![sentinal流控](D:\lys\java\project\Seckill-cloud\sentinal流控.png)



### 项目亮点

1. 使用ConcurrentHashMap进行内存标记+Redis缓存的二级缓存方式减轻数据库访问压力，提高查询效率
2. 使用RabbitMQ进行异步下单，提高响应速度，提升用户体验
3. 借助于Spring Cloud Alibaba Sentinel进行限流，该工具具有良好的页面控制功能，便于控制。



### 项目难点及出现的问题

1. **RabbitMQ消息重复消费怎么办？**

   在数据库中将订单编号与用户id设置为唯一索引，同一个用户的订单不能重新生成，因此防止消息重复。

2. **RabbitMQ怎么确保消息不丢失？**

   生产端发布确认机制，消费端消息应答。

3. **加乐观锁会出现库存遗留问题？**

   加乐观锁更新时需要对比版本号，导致很多请求失败，因此可能出现库存遗留，因此改用悲观锁解决此问题，在读多写多的场景下乐观锁并不适用。

4. **如何解决超卖问题？**

   Redis端：直接上分布式锁

   数据库端：不加锁，在更新语句的时候也判断下库存是否小于零，小于零则秒杀失败，大于零则秒杀成功。

5. **ConcurrentHashMap进行内存标记分布式场景下会出现数据不同步的情况，怎么解决？**

   通过zookeeper进行集群同步：zookeeper创建节点，集群服务监听这个节点，节点数据为库存是否不够，当ConcurrentHashMap内存标记为true时，集群其他服务监听到节点数据变化就更新本地ConcurrentHashMap的数据


Redis缓存出现的问题：https://www.yuque.com/u22408961/gnmalc/ako710