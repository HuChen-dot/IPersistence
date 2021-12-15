package com.hu.test.mapper;



import com.hu.test.pojo.User;

import java.util.List;

/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/10/31 1:27 下午
 **/
public interface UserMapper {


    /**
     * 根据ID查询单个
     * @return
     */
    User selectOne(User user);


    /**
     * 查询所有
     * @return
     */
    List<User> selectList();

    /**
     * 根据参数查询，拼接查询条件
     * @return
     */
    List<User> selectListByParam(User user);


    /**
     * 添加不为null的属性
     * @param user
     * @return
     */
    int add(User user);


    /**
     * 修改
     * @param user
     * @return
     */
    int update(User user);


    /**
     * 删除
     * @param user
     * @return
     */
    int delete(User user);

}
