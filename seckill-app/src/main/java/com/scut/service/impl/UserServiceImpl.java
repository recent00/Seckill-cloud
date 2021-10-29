package com.scut.service.impl;

import com.scut.entity.User;
import com.scut.exception.DAOException;
import com.scut.exception.ServiceException;
import com.scut.mapper.UserMapper;
import com.scut.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public int addUser(User user) throws ServiceException {
        if(StringUtils.isEmpty(user.getUserName())) throw new ServiceException("name不能为空");
        if (StringUtils.isEmpty(user.getPhone())) {
            throw new ServiceException("phone不能为空");
        }
        try {
            return userMapper.addUser(user);
        }catch (DAOException d) {
            throw new ServiceException("由DAOException导致");
        }
    }

    @Override
    public User getUserById(Integer id) throws ServiceException {
        if(id == null) {
            throw new ServiceException("id不能为空");
        }
        try {
            return userMapper.getUserById(id);
        } catch (DAOException d) {
            throw new ServiceException("由DAOException导致");
        }
    }
}
