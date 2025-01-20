package com.axr.stockmanage.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
