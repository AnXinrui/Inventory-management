package com.axr.stockmanage.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author xinrui.an
 * @date 2025/01/21
 */
@Data
@Builder
public class StockDTO {

    private Long productId;

    private Integer updateQuantity;

}
