package com.axr.stockmanage.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xinrui.an
 * @date 2025/01/21
 */
@Data
public class ProductAddDTO {

    private String name;

    private BigDecimal price;

}
