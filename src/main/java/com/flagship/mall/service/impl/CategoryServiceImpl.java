package com.flagship.mall.service.impl;

import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.dao.CategoryMapper;
import com.flagship.mall.model.pojo.Category;
import com.flagship.mall.model.request.AddCategoryReq;
import com.flagship.mall.model.vo.CategoryVO;
import com.flagship.mall.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Flagship
 * @Date 2021/3/25 9:07
 * @Description 分类service实现类
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

    /**
     * 更新分了
     *
     * @param updateCategory 要更新的分类对象
     */
    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null) {
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())) {
                throw new FlagshipMallException(FlagshipMallExceptionEnum.NAME_EXISTED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 删除分类
     *
     * @param id 分类id
     */
    @Override
    public void delete(Integer id) {
//        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
//        //查不到记录，删除失败
//        if (categoryOld == null) {
//            throw new FlagshipMallException(FlagshipMallExceptionEnum.DELETE_FAILED);
//        }
//        int count = categoryMapper.deleteByPrimaryKey(id);
//        if (count == 0) {
//            throw new FlagshipMallException(FlagshipMallExceptionEnum.DELETE_FAILED);
//        }

        if (categoryMapper.selectByPrimaryKey(id) == null || categoryMapper.deleteByPrimaryKey(id) == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 查询分类列表（管理员）
     *
     * @param pageNum  页码
     * @param pageSize 每页的数量
     * @return CategoryVO列表
     */
    @Override
    public PageInfo<Category> listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        return pageInfo;
    }

    /**
     * 查询分类列表（管理员）
     *
     * @return CategoryVO列表
     */
    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer() {
        ArrayList<CategoryVO> categoryVOArrayList = new ArrayList<>();
        recursivelyFindCategories(categoryVOArrayList, 0);
        return categoryVOArrayList;
    }

    private void recursivelyFindCategories(List<CategoryVO> categoryVOArrayList, Integer parentId) {
        //递归所有子类别，并组合成为一个分类树
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOArrayList.add(categoryVO);
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
