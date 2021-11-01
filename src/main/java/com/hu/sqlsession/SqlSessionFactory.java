package com.hu.sqlsession;

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
