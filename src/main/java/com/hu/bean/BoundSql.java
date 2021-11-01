package com.hu.bean;

import java.util.List;
/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/10/30 11:52 下午
 **/
public class BoundSql {

    private String sql;

    private List<ParameterMapping> parameterMappingist ;

    public BoundSql(String sql, List<ParameterMapping> parameterMappingist) {
        this.sql = sql;
        this.parameterMappingist = parameterMappingist;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappingist() {
        return parameterMappingist;
    }

    public void setParameterMappingist(List<ParameterMapping> parameterMappingist) {
        this.parameterMappingist = parameterMappingist;
    }
}
