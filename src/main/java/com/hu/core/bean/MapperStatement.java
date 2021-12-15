package com.hu.core.bean;

import org.dom4j.Element;

/**
 * @Author: hu.chen
 * @Description: mapper+sql语句配置文件存放类
 * @DateTime: 2021/10/30 2:40 下午
 **/
public class MapperStatement {

    /**
     * 当前sql语句的id
     */
    private String id;

    /**
     * sql语句
     */
    private String sql;

    /**
     * 返回值类型
     */
    private String resultType;

    /**
     * 参数类型
     */
    private String parameterType;

    /**
     * 当前节点
     */
    private Element element;

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterTyp(String parameterTyp) {
        this.parameterType = parameterTyp;
    }
}
