package com.ariplaza.stockmaster.entity.dto;

import com.ariplaza.stockmaster.entity.Inventory;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class InventoryDto {
    public Long id;

    public Long productId;

    public String productName;

    public String warehouseName;

    public Integer stockCount;

    public Integer minStock;

    public LocalDateTime createTime;

    public LocalDateTime lastUpdateTime;

    public LocalDateTime deleteTime;

    public Boolean isActive;

    public String stockStatus;
    public InventoryDto(Inventory inventory){
        this.id=inventory.getId();
        this.productId=inventory.getProductId();
        this.productName=inventory.getProductName();
        this.warehouseName=inventory.getWarehouseName();
        this.stockCount=inventory.getStockCount();
        this.minStock=inventory.getMinStock();
        this.createTime=inventory.getCreateTime();
        this.lastUpdateTime=inventory.getLastUpdateTime();
        this.deleteTime=inventory.getDeleteTime();
        this.isActive=inventory.getIsActive();
        this.stockStatus=this.stockCount>=this.minStock?"充足":"不足";
    }
}
