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
 * 
 * </p>
 *
 * @author yyj
 * @since 2025-12-30
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("stock_master_suppliers")
public class Suppliers implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private LocalDateTime deleteTime;

    private Boolean isActive;


}
