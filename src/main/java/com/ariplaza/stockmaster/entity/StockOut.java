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
 * 销售出库流水表
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("stock_master_stock_out")
public class StockOut implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Integer quantity;

    private String orderNo;

    private Long operator;

    private LocalDateTime createTime;

    private BigDecimal salePrice;

    private BigDecimal totalSale;

    private LocalDateTime deleteTime;


}
