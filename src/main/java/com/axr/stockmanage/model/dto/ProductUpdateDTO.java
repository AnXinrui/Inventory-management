package com.axr.stockmanage.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xinrui.an
 * @date 2025/01/23
 */
@Data
public class ProductUpdateDTO {

    private Long id;

    private String name;

    private BigDecimal price;
}
