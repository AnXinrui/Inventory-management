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