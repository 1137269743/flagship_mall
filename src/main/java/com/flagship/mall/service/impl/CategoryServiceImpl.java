package com.flagship.mall.service.impl;

import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.dao.CategoryMapper;
import com.flagship.mall.model.pojo.Category;
import com.flagship.mall.model.request.AddCategoryReq;
import com.flagship.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Flagship
 * @Date 2021/3/25 9:07
 * @Description
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     *
     * @param addCategoryReq 分类请求对象
     */
    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());
        if (categoryOld != null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.CREATE_FAILED);
        }
    }
}
