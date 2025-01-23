package com.axr.stockmanage.controller;

import com.axr.stockmanage.common.BusinessException;
import com.axr.stockmanage.common.Result;
import com.axr.stockmanage.model.dto.ProductPurchaseDTO;
import com.axr.stockmanage.model.dto.ProductUpdateDTO;
import com.axr.stockmanage.model.entity.Product;
import com.axr.stockmanage.model.dto.ProductAddDTO;
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
    public Result<Integer> addProduct(@RequestBody ProductAddDTO dto) {
        Integer id = productService.addProduct(dto);
        return Result.success(id);
    }

    @PostMapping("delete")
    public Result<Integer> deleteProduct(Integer id) {
        if (id == null) {
            throw new BusinessException("商品ID不能为空");
        }
        return Result.success(productService.deleteProduct(id));
    }

    @GetMapping("list")
    public Result<List<ProductVO>> listProduct(Product product) {
        return Result.success(productService.list(product));
    }

    @PostMapping("update")
    public Result<Integer> updateProduct(@RequestBody ProductUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessException("商品信息不完整");
        }
        return Result.success(productService.updateProduct(dto));
    }

    @PostMapping("purchase")
    public Result<ProductVO> productPurchase(@RequestBody ProductPurchaseDTO dto) {
        return Result.success(productService.purchaseProduct(dto));
    }

    @PostMapping("updateStatus")
    public Result<Boolean> updateStatus(Integer id) {
        return Result.success(productService.updateProductStatus(id));
    }
}
