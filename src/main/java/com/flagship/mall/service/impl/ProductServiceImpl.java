package com.flagship.mall.service.impl;

import com.flagship.mall.common.Constant;
import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.dao.ProductMapper;
import com.flagship.mall.model.pojo.Product;
import com.flagship.mall.model.query.ProductListQuery;
import com.flagship.mall.model.request.AddProductReq;
import com.flagship.mall.model.request.ProductListReq;
import com.flagship.mall.model.request.UpdateProductReq;
import com.flagship.mall.model.vo.CategoryVO;
import com.flagship.mall.service.CategoryService;
import com.flagship.mall.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Flagship
 * @Date 2021/3/26 8:09
 * @Description 商品service实现类
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增商品
     * @param addProductReq 新增数据对象
     */
    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product productOld = productMapper.selectByName(product.getName());
        if (productOld != null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.CREATE_FAILED);
        }
    }

    /**
     * 更新商品
     *
     * @param updateProductReq 新增商品对象
     */
    @Override
    public void update(UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        Product oldProduct = productMapper.selectByName(product.getName());
        if (oldProduct != null && !oldProduct.getId().equals(product.getId())) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 删除商品
     *
     * @param id 商品id
     */
    @Override
    public void delete(Integer id) {
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 状态上下架
     * @param ids 商品id列表
     * @param status 商品状态
     */
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer status) {
        int count = productMapper.batchUpdateSellStatus(ids, status);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 查询商品列表（管理员）
     *
     * @param pageNum  页号
     * @param pageSize 每页的行数
     * @return 商品列表
     */
    @Override
    public PageInfo<Product> listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo<Product> pageInfo = new PageInfo<Product>(products);
        return pageInfo;
    }

    /**
     * 获取商品细节
     *
     * @param id 商品id
     * @return 商品对象
     */
    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    /**
     * 查询商品列表（用户）
     *
     * @param productListReq 商品列表请求对象
     * @return 商品列表
     */
    @Override
    public PageInfo<Product> list(ProductListReq productListReq) {
        //构建Query对象
        ProductListQuery productListQuery = new ProductListQuery();

        //搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        //目录处理：递归查询当前分类包含子分类下的商品
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOS, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        //排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo<Product> pageInfo = new PageInfo<>(productList);
        return pageInfo;
    }

    /**
     * 递归获取所有的分类id
     * @param categoryVOList CategoryVO列表
     * @param categoryIds 商品id列表
     */
    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
        for (CategoryVO categoryVO : categoryVOList) {
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }
}
