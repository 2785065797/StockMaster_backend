package com.ariplaza.stockmaster;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ariplaza.stockmaster.mapper")
public class StockMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockMasterApplication.class, args);
    }

}
