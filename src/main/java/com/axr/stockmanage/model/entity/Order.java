package com.axr.stockmanage.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author xinrui.an
 * @LocalDateTime 2025/02/11
 */
@Data
public class Order implements Serializable {

    /**
     * 主键ID
     */

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    private Long productId;

    private Long shopId;


    /**
     * 订单状态， 10 已新建， 20 已支付，90 已完成，0 已取消
     */
    private Integer status;

    /**
     * 订单金额
     */
    private BigDecimal payMoney;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 创建人ID
     */
    private String createId;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人ID
     */
    private String modifyId;

    /**
     * 修改人
     */
    private String modifyName;

    /**
     * 更新时间
     */
    private LocalDateTime modifyTime;

    /**
     * 版本号，用作乐观锁控制
     */
    private Integer version;
}