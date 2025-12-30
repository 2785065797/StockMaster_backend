package com.ariplaza.stockmaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品核心数据表
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("stock_master_products")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer categoryId;

    private BigDecimal costPrice;

    private BigDecimal preSalePrice;

    private BigDecimal currentPrice;

    private String unit;

    private String imagePath;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private LocalDateTime deleteTime;

    private Boolean isActive;


}
