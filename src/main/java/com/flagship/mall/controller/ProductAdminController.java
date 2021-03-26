package com.flagship.mall.controller;

import com.flagship.mall.common.ApiRestResponse;
import com.flagship.mall.common.Constant;
import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.request.AddProductReq;
import com.flagship.mall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * @Author Flagship
 * @Date 2021/3/26 8:01
 * @Description
 */
@RestController
public class ProductAdminController {
    @Autowired
    private ProductService productService;

    /**
     * 增加商品
     * @param addProductReq 增加商品对象
     * @return 统一响应对象
     */
    @PostMapping("/admin/product")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    /**
     * 上传文件接口
     * @param httpServletRequest http请求对象
     * @param file 上传文件
     * @return 统一响应对象
     */
    @PostMapping("/admin/upload")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest,@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        String suffixName = filename.substring(filename.lastIndexOf("."));
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new FlagshipMallException(FlagshipMallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL().toString())) + "/images/" + newFileName);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return ApiRestResponse.error(FlagshipMallExceptionEnum.UPLOAD_FAILED);
        }
    }

    /**
     * 把uir多余的部分剔除，构造出想要的uri
     * @param uri 原始uri
     * @return 目标uri
     */
    private URI getHost(URI uri) {
        URI effectiveUri = null;
        try {
            effectiveUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return effectiveUri;
    }
}
