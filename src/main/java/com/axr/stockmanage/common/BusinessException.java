package com.axr.stockmanage.common;

/**
 * 自定义业务异常类
 *
 * @author xinrui.an
 * @date 2025/01/21
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
