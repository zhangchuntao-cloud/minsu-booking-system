package com.code.web_homework.service;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.entity.HouseCategory;
import com.code.web_homework.mapper.HouseCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房源分类业务逻辑层。
 */
@Service
public class HouseCategoryService {

    private final HouseCategoryMapper categoryMapper;

    public HouseCategoryService(HouseCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /** 查询全部启用中的分类 */
    public List<HouseCategory> listAll() {
        return categoryMapper.selectAll();
    }

    /** 查询分类及其房源数量，用于后台分类管理页与统计图 */
    public List<HouseCategory> listWithCount() {
        return categoryMapper.selectWithHouseCount();
    }

    /** 根据主键查询 */
    public HouseCategory getById(Long id) {
        return categoryMapper.selectById(id);
    }

    /** 新增分类 */
    @Transactional(rollbackFor = Exception.class)
    public void save(HouseCategory category, Long operatorId) {
        validate(category);
        category.setCreatedBy(operatorId);
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(99);
        }
        categoryMapper.insert(category);
    }

    /** 修改分类 */
    @Transactional(rollbackFor = Exception.class)
    public void update(HouseCategory category, Long operatorId) {
        HouseCategory exist = categoryMapper.selectById(category.getId());
        if (exist == null) {
            throw new BusinessException("分类不存在");
        }
        validate(category);
        category.setModifyBy(operatorId);
        categoryMapper.update(category);
    }

    /** 删除分类 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        HouseCategory exist = categoryMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    private void validate(HouseCategory category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new BusinessException("请填写分类名称");
        }
        if (category.getCategoryName().length() > 30) {
            throw new BusinessException("分类名称不能超过 30 个字");
        }
    }
}
