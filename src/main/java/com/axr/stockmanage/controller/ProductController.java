package com.axr.stockmanage.controller;

import com.axr.stockmanage.common.Result;
import com.axr.stockmanage.model.Product;
import com.axr.stockmanage.model.dto.ProductDTO;
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
    public Result<Integer> addProduct(@RequestBody ProductDTO dto) {
        Integer id = productService.addProduct(dto);
        return Result.success(id);
    }

    @PostMapping("delete")
    public Result<Integer> deleteProduct(Integer id) {
        return Result.success(productService.deleteProduct(id));
    }

    @GetMapping("list")
    public Result<List<Product>> listProduct() {
        return Result.success(productService.listAll());
    }

    @PostMapping("update")
    public Result<Integer> updateProduct(@RequestBody Product product) {
        return Result.success(productService.updateProduct(product));
    }

    @PostMapping("updateStatus")
    public Result<Boolean> updateStatus(Integer id) {
        return Result.success(productService.updateProductStatus(id));
    }
}
