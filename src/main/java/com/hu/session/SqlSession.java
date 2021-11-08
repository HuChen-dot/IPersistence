package com.hu.session;

import java.util.List;

/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/10/30 3:19 下午
 **/
public interface SqlSession {

    /**
     * 查询单个
     *
     * @param <T>
     * @return
     */
    <T> T selectOne(String statemenId, Object... param) throws  Exception;

    /**
     * 查询多个
     *
     * @param <T>
     * @return
     */
    <T> List<T> selectList(String statemenId, Object... param) throws Exception;


    /**
     * 修改
     *
     * @param statemenId
     * @param param
     * @return
     */
    int update(String statemenId, Object... param) throws Exception;


    /**
     * 添加
     *
     * @param statemenId
     * @param param
     * @return
     */
    int insert(String statemenId, Object... param) throws Exception;

    /**
     * 删除
     *
     * @param statemenId
     * @param param
     * @return
     */
    int delete(String statemenId, Object... param) throws Exception;

    /**
     * 获取代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> clazz);

    Object doExecutor(String typeName,String statemenId, Object... param) throws Exception;
}
