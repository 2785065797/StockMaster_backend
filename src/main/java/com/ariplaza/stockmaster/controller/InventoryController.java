package com.ariplaza.stockmaster.controller;

import com.ariplaza.stockmaster.entity.Inventory;
import com.ariplaza.stockmaster.entity.dto.InventoryDto;
import com.ariplaza.stockmaster.entity.dto.PageDto;
import com.ariplaza.stockmaster.service.IInventoryService;
import com.ariplaza.stockmaster.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 实时库存管理表 前端控制器
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    IInventoryService inventoryService;
    @GetMapping("/refresh")
    public ResponseEntity<?> RefreshData(@RequestParam(defaultValue = "1")int page,
                                      @RequestParam(defaultValue = "10")int pageSize,
                                      @RequestParam(required = false)String searchQuery) {
        PageDto pageDtos=inventoryService.Refresh(page, pageSize, searchQuery);
        //return ResponseUtil.success("リフレッシュ成功",pageDtos);
        if (pageDtos.getTotal()==0||(pageDtos.getList()!=null&&!pageDtos.getList().isEmpty())){
            return ResponseUtil.success("リフレッシュ成功",pageDtos);
        }else{
            return ResponseUtil.error(406,"リフレッシュ失敗");
        }
    }


}
