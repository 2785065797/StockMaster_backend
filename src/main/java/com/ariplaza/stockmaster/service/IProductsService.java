package com.ariplaza.stockmaster.service;

import com.ariplaza.stockmaster.entity.Products;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * <p>
 * 商品核心数据表 服务类
 * </p>
 *
 * @author yyj
 * @since 2025-12-30
 */
public interface IProductsService extends IService<Products> {

    BigDecimal parsePrice(String costPriceStr);

    boolean saveProduct(String name, Integer categoryId, String unit, BigDecimal currentPrice, BigDecimal preSalePrice, BigDecimal costPrice, Boolean isActive, String description, MultipartFile image);
}
