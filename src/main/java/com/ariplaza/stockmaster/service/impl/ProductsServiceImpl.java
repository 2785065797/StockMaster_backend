package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.Products;
import com.ariplaza.stockmaster.mapper.ProductsMapper;
import com.ariplaza.stockmaster.service.IProductsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品核心数据表 服务实现类
 * </p>
 *
 * @author yyj
 * @since 2025-12-30
 */
@Service
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements IProductsService {

}
