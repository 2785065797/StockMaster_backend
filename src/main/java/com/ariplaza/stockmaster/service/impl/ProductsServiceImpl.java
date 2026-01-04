package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.Products;
import com.ariplaza.stockmaster.mapper.ProductsMapper;
import com.ariplaza.stockmaster.service.IProductsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * <p>
 * 商品核心数据表 服务实现类
 * </p>
 *
 * @author yyj
 * @since 2025-12-30
 */
@Service
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements IProductsService {

    @Value("${images_upload_dir}")
    private String imageUploadDir;

    @Override
    public BigDecimal parsePrice(String costPriceStr) {
        if(costPriceStr!=null&&!costPriceStr.isEmpty()){
            return new BigDecimal(costPriceStr);
        }else {
            return null;
        }
    }

    @Override
    public boolean saveProduct(String name, Integer categoryId, String unit, BigDecimal currentPrice, BigDecimal preSalePrice, BigDecimal costPrice, Boolean isActive, String description, MultipartFile image) {
        String imagePath=null;
        if(image!=null&&!image.isEmpty()){
            try{
                imagePath = saveImage(image);
            }catch (Exception e){
                System.out.println("图像保存失败");
                return false;
            }
        }
        Products product = new Products(name, categoryId, unit, currentPrice, preSalePrice, costPrice, isActive, description,imagePath);
        return save(product);
    }

    private String saveImage(MultipartFile image)throws Exception {
        String fileName= UUID.randomUUID()+"_"+image.getOriginalFilename();
        Path path= Paths.get(imageUploadDir,fileName);
        try{
            image.transferTo(path);
        }catch (IOException e){
            throw new IOException("图像保存失败:"+fileName,e);
        }
        return path.toString();
    }


}
