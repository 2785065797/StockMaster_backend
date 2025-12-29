package com.ariplaza.stockmaster.service.impl;

import com.ariplaza.stockmaster.entity.User;
import com.ariplaza.stockmaster.mapper.UserMapper;
import com.ariplaza.stockmaster.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存系统用户表 服务实现类
 * </p>
 *
 * @author yyj
 * @since 2025-12-26
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User getUserbyUsername(String username) {
        return lambdaQuery().eq(User::getUsername,username).one();
    }

    @Override
    public boolean registerUser(String username, String email, String password) {
        if(getUserbyUsername(username)!=null){
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return save(user);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        return true;
    }

}
