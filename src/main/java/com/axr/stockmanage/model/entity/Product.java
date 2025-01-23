package com.axr.stockmanage.model.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@Data
@Builder
public class Product {

    private Integer id;

    private String name;

    private BigDecimal price;

    /**
     * 商品状态，0 表示下架，1 表示上架
     */
    private Integer status;

}
