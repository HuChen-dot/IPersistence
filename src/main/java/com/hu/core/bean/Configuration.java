package com.hu.core.bean;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: hu.chen
 * @Description: 核心配置类，存放mybatis核心配置
 * @DateTime: 2021/10/30 2:36 下午
 **/
public class Configuration {
    /**
     * 数据源
     */
    private DataSource dataSource;

    /**
     * key: namespace+id
     * value:具体的sql语句对象
     */
    private Map<String, MapperStatement> mappers=new ConcurrentHashMap<>();






    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MapperStatement> getMappers() {
        return mappers;
    }

    public void setMappers(Map<String, MapperStatement> mappers) {
        this.mappers = mappers;
    }

}
