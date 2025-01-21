package com.axr.stockmanage.model;

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
    private int status;
}
