package com.scut.mapper;

import com.scut.entity.User;
import com.scut.exception.DAOException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    int addUser(User user) throws DAOException;

    /**
     * 批量插入
     * @param userList
     * @return
     */
    int batchInsert(@Param("userList") List<User> userList) throws DAOException;

    User getUserById(Integer id) throws DAOException;

}
