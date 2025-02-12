package com.axr.stockmanage.controller;

import com.axr.stockmanage.annotation.Limit;
import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.common.Result;
import com.axr.stockmanage.model.dto.ProductAddDTO;
import com.axr.stockmanage.model.dto.ProductPurchaseDTO;
import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.entity.Product;
import com.axr.stockmanage.model.vo.ProductVO;
import com.axr.stockmanage.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xinrui.an
 * @date 2025/01/21
 */
@Slf4j
@RestController
@RequestMapping("product")
public class ProductController {

    @Resource
    private ProductService productService;


    @PostMapping("add")
    public Result<Long> addProduct(@RequestBody ProductAddDTO dto) {
        long id = productService.addProduct(dto);
        return Result.success(id);
    }

    @PostMapping("delete")
    public Result<Integer> deleteProduct(Long id) {
        if (id == null) {
            throw new BusinessException("商品ID不能为空");
        }
        return Result.success(productService.deleteProduct(id));
    }

    @GetMapping(value = "/{id}")
    @CircuitBreaker(name = "viewCountCircuitBreaker", fallbackMethod = "fallbackGetProduct")
    public Result<Product> findProduct(@PathVariable Long id) {

        // 如果浏览人数超过阈值，则触发降级
        if (!productService.checkViewCount(id)) {
            throw new BusinessException("浏览人数过多，已触发降级");
        }

        Product product = productService.findById(id);
        return Result.success(product);
    }

    @GetMapping("list")
    public Result<List<ProductVO>> listProducts(Product product) {
        return Result.success(productService.list(product));
    }

    @GetMapping("listAll")
    public Result<List<ProductVO>> listAllProducts() {
        return Result.success(productService.listAll());
    }

    @PostMapping("update")
    public Result<Integer> updateProduct(@RequestBody ProductUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessException("商品信息不完整");
        }
        return Result.success(productService.updateProduct(dto));
    }

    @PostMapping("purchase")
    public Result<Boolean> productPurchase(@RequestBody ProductPurchaseDTO dto) {
        return Result.success(productService.purchaseProduct(dto));
    }

    @Limit(key = "scekill", permitsPerSecond = 10)
    @PostMapping("scekill/{id}")
    public Result<Long> secKillProduct(@PathVariable Long id) {
        if (null == id || id <= 0) {
            throw new BusinessException("商品 ID 异常");
        }
        return Result.success(productService.secKillProduct(id));
    }

    @PostMapping("updateStatus")
    public Result<Boolean> updateStatus(Long id) {
        if (id == null) {
            throw new BusinessException("商品ID不能为空");
        }
        return Result.success(productService.updateProductStatus(id));
    }

    @GetMapping("/viewCount/{id}")
    public Result<Long> getViewCount(@PathVariable Long id) {
        return Result.success(productService.getViewCount(id));
    }

    /**
     * 降级方法
     * @param id 商品ID
     * @param throwable 发生的异常
     * @return 降级结果
     */
    public Result<Product> fallbackGetProduct(Long id, Throwable throwable) {
        // 记录降级信息
        log.warn("进入降级方法，商品ID = {}, 错误原因: {}", id, throwable.getMessage());

        // 可以返回降级的商品数据，或者一个简单的提示信息
        return Result.fail("当前商品浏览人数过多，服务暂时不可用，请稍后再试");
    }

}
