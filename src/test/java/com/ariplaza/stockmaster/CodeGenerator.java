package com.ariplaza.stockmaster;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/stock_master_db?useSSL=false&serverTimezone=UTC",
                        "virtual", "1")
                .globalConfig(builder -> builder
                        .author("yyj")
                        .outputDir("C:\\Users\\yyj\\Desktop\\stu\\Java\\StockMaster\\src\\main\\java")
                        .commentDate("yyyy-MM-dd")
                        .disableOpenDir()
                )
                .packageConfig(builder -> builder
                        .parent("com.ariplaza.stockmaster")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                "C:\\Users\\yyj\\Desktop\\stu\\Java\\StockMaster\\src\\main\\resources\\mapper"
                        ))
                )
                .strategyConfig(builder -> builder
                        .addTablePrefix("stock_master_") // 修正：使用表前缀匹配
                        .entityBuilder() // 必须先调用entityBuilder()
                        .enableLombok()
                        .enableChainModel()
                        .naming(NamingStrategy.underline_to_camel) // 这里必须在entityBuilder()内部
                        .controllerBuilder()
                        .enableRestStyle()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}