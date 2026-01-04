package com.ariplaza.stockmaster.controller;

import com.ariplaza.stockmaster.service.IProductsService;
import com.ariplaza.stockmaster.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * <p>
 * 商品核心数据表 前端控制器
 * </p>
 *
 * @author yyj
 * @since 2025-12-30
 */
@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    IProductsService productsService;
    @PostMapping("/insert")
    public ResponseEntity<?> productInsert(
            @RequestParam("name") String name,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("unit") String unit,
            @RequestParam("currentPrice") String currentPriceStr,
            @RequestParam(value = "preSalePrice", required = false) String preSalePriceStr,
            @RequestParam("costPrice") String costPriceStr,
            @RequestParam("isActive") String isActiveStr,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image){
        BigDecimal costPrice=productsService.parsePrice(costPriceStr);
        BigDecimal preSalePrice=productsService.parsePrice(preSalePriceStr);
        BigDecimal currentPrice=productsService.parsePrice(currentPriceStr);
        if(costPrice!=null&&preSalePrice!=null&&currentPrice!=null){
            Boolean isActive=Boolean.parseBoolean(isActiveStr);
            boolean b = productsService.saveProduct(name, categoryId, unit, currentPrice, preSalePrice, costPrice, isActive, description, image);
            if(b){
                return ResponseUtil.success("商品添加成功");
            }
        }
        return ResponseUtil.error(407,"商品添加失败");
    }
}
