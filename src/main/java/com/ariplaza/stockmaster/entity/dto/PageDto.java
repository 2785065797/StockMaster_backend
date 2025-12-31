package com.ariplaza.stockmaster.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageDto {
    List<InventoryDto> list;
    long total;

    public PageDto(List<InventoryDto> list, long total) {
        this.list=list;
        this.total=total;
    }
}
