## Assignment3 Spring & MyBatis
利用Spring & MyBatis所学知识实现一个简单的商品库存管理系统。
要求：
（1）商品管理：实现商品查询、增加、删除、修改、上下架管理
（2）库存管理：实现对商品库存的增加、减少和查询等功能
（3）需体现数据库访问、Spring事务的应用、异常处理等

#### 1. 库表创建
```mysql
CREATE DATABASE StockManagementDB;

USE StockManagementDB;
```

**商品表(product)**
```mysql
CREATE TABLE t_product
(
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  name VARCHAR(255) NOT NULL COMMENT '商品名称',
  price DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
  status TINYINT(1) DEFAULT 0 NOT NULL COMMENT '商品状态，0表示下架，1表示上架',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id) USING BTREE,
  UNIQUE KEY uniq_name(name)
) ENGINE=InnoDB COMMENT = '商品表（学习使用 axr)';

```
**库存表 (stock)**

```mysql
CREATE TABLE t_stock
(
  id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '库存记录ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  quantity INT NOT NULL COMMENT '库存数量',
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id) USING BTREE,
  UNIQUE KEY uniq_product_id(product_id)
) ENGINE=InnoDB COMMENT = '库存表（学习使用 axr)';

```

**订单表**
```mysql
CREATE TABLE `t_order` (
  `id` bigint unsigned NOT NULL COMMENT '主键ID',
  `user_id` bigint  NOT NULL COMMENT '用户id',
  `shop_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '门店ID',
  `product_id` bigint  NOT NULL COMMENT '商品id',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '订单状态， 10 已新建， 20 已支付，90 已完成，0 已取消 ',
  `pay_money` decimal(12,4) unsigned NOT NULL COMMENT '订单金额',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_id` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_name` varchar(60) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_id` varchar(32) DEFAULT NULL COMMENT '修改人ID',
  `modify_name` varchar(60) DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='订单主表';
```

#### 核心功能：
- 商品
  - 新增商品 （默认库存为 0）
    - 通过数据库中对字段加上唯一约束，保证接口幂等性
  - 删除商品
  - 修改商品信息
  - 上下架管理
  - 查询商品 （根据商品名称或id）
  - 购买商品
    - 使用悲观锁（结合 `FOR UPDATE`），保证线程安全。
- 库存
  - 增加或减少库存
  - 查询库存
- 秒杀功能： redis 进行校验 -> 消息队列 -> 数据库扣减库存，生成订单
  - 基于乐观锁CAS 方法进行库存扣减
- 限流：使用RateLimiter结合AOP实现自定义限流注解
- 降级：采用 resilience4j 实现降级
- 统计访问量： redis 进行统计