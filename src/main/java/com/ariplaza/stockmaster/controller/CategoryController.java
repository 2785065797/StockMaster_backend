package com.ariplaza.stockmaster.controller;

import com.ariplaza.stockmaster.entity.Category;
import com.ariplaza.stockmaster.service.ICategoryService;
import com.ariplaza.stockmaster.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 多级商品分类表 前端控制器
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;

    @PostMapping("/insert")
    public ResponseEntity<?> categoryInsert(@RequestBody Map<String,String> category){
        boolean b=categoryService.insert(category);
        if(b){
            return ResponseUtil.success("分类新增成功");
        }else {
         return ResponseUtil.error(408,"分类新增失败");
        }
    }
    @GetMapping("/fetch")
    public ResponseEntity<?> categoryFetch(){
        List<Category> list=categoryService.list();
        if(list!=null){
            return ResponseUtil.success("商品列表刷新成功",list);
        }else{
            return ResponseUtil.error(409,"商品列表刷新失败");
        }
    }
}
