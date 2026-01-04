package com.ariplaza.stockmaster.service;

import com.ariplaza.stockmaster.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 多级商品分类表 服务类
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
public interface ICategoryService extends IService<Category> {

    boolean insert(Map<String, String> category);
}
