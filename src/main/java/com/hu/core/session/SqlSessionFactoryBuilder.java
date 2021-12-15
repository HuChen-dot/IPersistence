package com.hu.core.session;

import com.hu.core.bean.Configuration;
import com.hu.core.config.core.XmlCoreConfigBuilder;

import java.io.InputStream;

/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/10/30 3:18 下午
 **/
public class SqlSessionFactoryBuilder {


    public SqlSessionFactory builder(InputStream in) throws Exception {
        XmlCoreConfigBuilder xmlCoreConfigBuilder=new XmlCoreConfigBuilder();


        Configuration configuration = xmlCoreConfigBuilder.parseConfig(in);



        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return defaultSqlSessionFactory ;
    }
}
