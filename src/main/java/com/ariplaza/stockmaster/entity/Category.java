package com.ariplaza.stockmaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 多级商品分类表
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("stock_master_category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private LocalDateTime deleteTime;

    private Boolean isActive;

    private String description;

    public Category(String name,Boolean isActive,String description){
        this.name=name;
        this.isActive=isActive;
        this.description=description;
    }
    public Category(){}
}
