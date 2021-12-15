package com.hu.core.session;

/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/10/30 3:19 下午
 **/
public interface SqlSessionFactory {

    /**
     * 获取sqlSession
     * @return
     */
    SqlSession openSqlSession();
}
