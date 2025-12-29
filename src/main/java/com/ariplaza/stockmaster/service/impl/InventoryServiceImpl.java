package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.Inventory;
import com.ariplaza.stockmaster.mapper.InventoryMapper;
import com.ariplaza.stockmaster.service.IInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 实时库存管理表 服务实现类
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@Service
@Transactional
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

}
