package com.demo.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.mes.common.exception.BusinessException;
import com.demo.mes.common.result.PageResult;
import com.demo.mes.common.result.Result;
import com.demo.mes.entity.Bom;
import com.demo.mes.entity.Material;
import com.demo.mes.mapper.BomMapper;
import com.demo.mes.mapper.MaterialMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物料管理")
@RestController
@RequestMapping("/material")
public class MaterialController {

    private final MaterialMapper materialMapper;
    private final BomMapper bomMapper;

    @Autowired
    public MaterialController(MaterialMapper materialMapper, BomMapper bomMapper) {
        this.materialMapper = materialMapper;
        this.bomMapper = bomMapper;
    }

    @Operation(summary = "物料分页列表")
    @GetMapping("/list")
    public Result<PageResult<Material>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        Page<Material> pageObj = new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 100));
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Material::getMaterialCode, keyword).or().like(Material::getMaterialName, keyword));
        }
        wrapper.orderByDesc(Material::getCreateTime);
        materialMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(pageObj.getRecords(), pageObj.getTotal(), page, size));
    }

    @Operation(summary = "全部物料")
    @GetMapping("/all")
    public Result<List<Material>> all() {
        return Result.success(materialMapper.selectList(
                new LambdaQueryWrapper<Material>().eq(Material::getStatus, 1)));
    }

    @Operation(summary = "物料详情")
    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        return Result.success(material);
    }

    @Operation(summary = "新增物料")
    @PreAuthorize("hasAuthority('base:material') or hasAuthority('*:*:*')")
    @PostMapping
    public Result<Void> create(@RequestBody Material material) {
        if (material.getMaterialCode() == null || material.getMaterialCode().isBlank()) {
            throw new BusinessException("物料编码不能为空");
        }
        if (material.getMaterialName() == null || material.getMaterialName().isBlank()) {
            throw new BusinessException("物料名称不能为空");
        }
        materialMapper.insert(material);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "修改物料")
    @PreAuthorize("hasAuthority('base:material') or hasAuthority('*:*:*')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Material material) {
        Material existing = materialMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("物料不存在");
        }
        material.setId(id);
        materialMapper.updateById(material);
        return Result.success("修改成功", null);
    }

    @Operation(summary = "删除物料")
    @PreAuthorize("hasAuthority('base:material') or hasAuthority('*:*:*')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Material existing = materialMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("物料不存在");
        }
        materialMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "产品BOM列表")
    @GetMapping("/bom/{materialId}")
    public Result<List<Bom>> bomList(@PathVariable Long materialId) {
        List<Bom> list = bomMapper.selectList(
                new LambdaQueryWrapper<Bom>().eq(Bom::getParentMaterialId, materialId));
        // 批量查询子物料信息
        if (!list.isEmpty()) {
            List<Long> childIds = list.stream().map(Bom::getChildMaterialId).toList();
            List<Material> materials = materialMapper.selectBatchIds(childIds);
            java.util.Map<Long, Material> map = materials.stream()
                    .collect(java.util.stream.Collectors.toMap(Material::getId, m -> m));
            list.forEach(bom -> {
                Material child = map.get(bom.getChildMaterialId());
                if (child != null) {
                    bom.setChildMaterialCode(child.getMaterialCode());
                    bom.setChildMaterialName(child.getMaterialName());
                }
            });
        }
        return Result.success(list);
    }

    @Operation(summary = "新增BOM项")
    @PreAuthorize("hasAuthority('base:bom') or hasAuthority('*:*:*')")
    @PostMapping("/bom")
    public Result<Void> createBom(@RequestBody Bom bom) {
        bomMapper.insert(bom);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "删除BOM项")
    @PreAuthorize("hasAuthority('base:bom') or hasAuthority('*:*:*')")
    @DeleteMapping("/bom/{id}")
    public Result<Void> deleteBom(@PathVariable Long id) {
        bomMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
