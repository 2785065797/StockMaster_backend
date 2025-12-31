package com.ariplaza.stockmaster.service;

import com.ariplaza.stockmaster.entity.Inventory;
import com.ariplaza.stockmaster.entity.dto.PageDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 实时库存管理表 服务类
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
public interface IInventoryService extends IService<Inventory> {

    PageDto Refresh(int page, int pageSize, String searchQuery);
}
