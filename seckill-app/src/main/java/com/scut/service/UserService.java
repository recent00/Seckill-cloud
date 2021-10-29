package com.scut.service;

import com.scut.entity.User;
import com.scut.exception.ServiceException;

public interface UserService {
    /**
     * 新增用户
     * @param user
     * @return
     */
    int addUser(User user) throws ServiceException;

    /**
     * 通过id查找用户
     * @param id
     * @return
     */
    User getUserById(Integer id) throws ServiceException;
}
