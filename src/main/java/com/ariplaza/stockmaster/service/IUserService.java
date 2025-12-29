package com.ariplaza.stockmaster.service;

import com.ariplaza.stockmaster.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 库存系统用户表 服务类
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
public interface IUserService extends IService<User> {

    User getUserbyUsername(String username);

    boolean registerUser(String username, String email, String password);

    boolean resetPassword(String token, String newPassword);
}
