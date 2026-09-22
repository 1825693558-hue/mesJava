package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.Product;
import com.demo.mes.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "产品管理")
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Operation(summary = "产品分页列表")
    @GetMapping("/list")
    public Result<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer productType) {
        Page<Product> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Product::getProductCode, keyword).or().like(Product::getProductName, keyword));
        }
        if (productType != null) wrapper.eq(Product::getProductType, productType);
        wrapper.orderByDesc(Product::getCreateTime);
        productMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "全部产品（下拉用）")
    @GetMapping("/all")
    public Result<List<Product>> all() {
        return Result.success(productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1)));
    }

    @Operation(summary = "新增产品")
    @PreAuthorize("hasAuthority('base:product') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody Product product) {
        productMapper.insert(product);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改产品")
    @PreAuthorize("hasAuthority('base:product') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productMapper.updateById(product);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除产品")
    @PreAuthorize("hasAuthority('base:product') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
