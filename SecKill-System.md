# SecKill-System

本项目是基于Spring cloud Alibaba的高并发秒杀系统，用于个人学习。

### 项目结构

- 服务注册与发现中心：**Nacos**
- API网关：**Gateway**
- 缓存：**Redis**
- 消息队列：**RabbitMq**
- 数据库：**MySQl**
- 熔断与限流：**Sentinel**
- 分布式事务：**Seata**




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



