package com.hu.session;

import com.hu.bean.Configuration;

/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/10/30 8:02 下午
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory{

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public SqlSession openSqlSession(){

        return new DefaultSqlSession(configuration);
    }
}
