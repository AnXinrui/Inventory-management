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
CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    status TINYINT(1) DEFAULT 0 NOT NULL COMMENT '商品状态，0表示下架，1表示上架',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '商品创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '商品更新时间'
) COLLATE = utf8mb4_general_ci COMMENT = '商品表';
```
**库存表 (stock)**

```mysql
CREATE TABLE stock (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '库存记录ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '库存数量',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '库存更新时间',
    FOREIGN KEY (product_id) REFERENCES product(id)  
) COLLATE = utf8mb4_general_ci COMMENT = '库存表';
```

#### 核心功能：
- 商品
  - 新增商品 （默认库存为 0）
  - 删除商品
  - 修改商品信息
  - 上下架管理
  - 查询商品 （根据商品名称或id）
  - 购买商品
- 库存
  - 增加或减少库存
  - 查询库存