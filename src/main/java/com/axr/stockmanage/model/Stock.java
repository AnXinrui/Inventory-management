package com.axr.stockmanage.model;

import lombok.Data;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@Data
public class Stock {
    private Integer id;
    private Integer productId;
    private int quantity;
}
