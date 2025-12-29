package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.Product;
import com.ariplaza.stockmaster.mapper.ProductMapper;
import com.ariplaza.stockmaster.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品核心数据表 服务实现类
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@Service
@Transactional
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
