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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xinrui.an
 * @date 2025/01/21
 */
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
    public Result<Product> findProduct(@PathVariable Long id) {
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
}
