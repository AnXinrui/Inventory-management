package com.axr.stockmanage;

import com.axr.stockmanage.mapper.ProductMapper;
import com.axr.stockmanage.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * @author xinrui.an
 * @date 2025/01/20
 */
@SpringBootTest
public class ProductMapperTest {
    @Autowired
    private ProductMapper productMapper;

    @Test
    public void addProduct() {
        Product product = Product.builder()
                .price(BigDecimal.valueOf(100))
                .name("黑人牙膏")
                .build();
        productMapper.addProduct(product);
        System.out.println(product.getId());
    }

}
