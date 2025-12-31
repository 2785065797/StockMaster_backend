package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.Inventory;
import com.ariplaza.stockmaster.entity.dto.InventoryDto;
import com.ariplaza.stockmaster.entity.dto.PageDto;
import com.ariplaza.stockmaster.mapper.InventoryMapper;
import com.ariplaza.stockmaster.service.IInventoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    InventoryMapper inventoryMapper;
    @Override
    public PageDto Refresh(int page, int pageSize, String searchQuery) {
        Page<Inventory> pageObj=new Page<>(page,pageSize);
        LambdaQueryWrapper<Inventory> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(searchQuery!=null&&!searchQuery.isEmpty()){
            lambdaQueryWrapper.likeRight(Inventory::getProductName,searchQuery);
        }
        lambdaQueryWrapper.orderByDesc(Inventory::getLastUpdateTime);
        inventoryMapper.selectPage(pageObj,lambdaQueryWrapper);
        List<InventoryDto> list = pageObj.getRecords().stream().map(this::AddStatus).toList();
        return new PageDto(list,pageObj.getTotal());
    }

    public InventoryDto AddStatus(Inventory inventory){
        return new InventoryDto(inventory);
    }
}
