package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.Category;
import com.ariplaza.stockmaster.mapper.CategoryMapper;
import com.ariplaza.stockmaster.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 多级商品分类表 服务实现类
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
