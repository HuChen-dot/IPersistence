package com.hu.config.sql;

import com.hu.bean.MapperStatement;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description: sql<if></if>标签解析
 * @DateTime: 2021/10/30 9:04 下午
 **/
public class SqlAnalysis {


    public static String analysis(MapperStatement mapperStatement, Object... param) throws Exception {
        String sql = mapperStatement.getSql().trim().toLowerCase();
        String parameterTyp = mapperStatement.getParameterType();
        //参数类型为空则直接返回sql
        if (parameterTyp == null) {
            return sql;
        }
        if (sql.startsWith("insert")) {
            return insert(mapperStatement, param);
        }
        if (sql.startsWith("select") || sql.startsWith("delete")) {
            return select(mapperStatement, param);
        }

        return update(mapperStatement, param);
    }


    private static String update(MapperStatement mapperStatement, Object... param) throws Exception {
        Class<?> aClass = Class.forName(mapperStatement.getParameterType());
        String sql = mapperStatement.getSql().trim().toLowerCase();

        //查找是否有where
        boolean isWhere = false;
        String id = mapperStatement.getId();
        int whereindex = sql.indexOf("where");
        StringBuffer bufferSql =null;
        if(whereindex!=-1){
            bufferSql= new StringBuffer(sql.substring(0, whereindex));
        }else {
            bufferSql= new StringBuffer(sql);
        }

        Element rootElement = mapperStatement.getElement();
        List<Element> list = rootElement.selectNodes("//if");
        Object o = param[0];
        boolean falg = true;
        for (Element element : list) {
            //是否是当前节点
            if (!isPresentNode(element, id)) {
                continue;
            }
            String test = element.attributeValue("test").trim();
            String name = test.split("\\!|=")[0];
            name = name.trim();
            Field declaredField = aClass.getDeclaredField(name);
            declaredField.setAccessible(true);
            Object para = declaredField.get(o);

            if (para != null) {
                String textTrim = element.getTextTrim();
                if (textTrim.indexOf(",") != -1) {
                    bufferSql.append(" " + textTrim);

                } else {
                    //如果当前已经有where
                        if(falg){
                            if (bufferSql.indexOf(",", bufferSql.length() - 1) != -1) {
                                bufferSql.delete(bufferSql.length() - 1, bufferSql.length());
                            }
                            bufferSql.append(" where ");
                            falg=false;
                            isWhere=true;
                        }
                        bufferSql.append(" "+textTrim);

                }

            }
        }
        return deleteAnd(bufferSql);
    }



    /**
     * 去除where 后面的and
     * @return
     */
    private static String deleteAnd(StringBuffer sql){
        if(sql.indexOf("where")!=-1) {
            StringBuffer buffer = new StringBuffer(sql.substring(0, sql.indexOf("where") + 5));
            String itme = sql.substring(sql.indexOf("where") + 5).trim();
            if (itme.startsWith("and")) {
                buffer.append(itme.substring(itme.indexOf("and") + 3));
            } else {
                buffer.append(" " + itme);
            }
            return buffer.toString();
        }else {
            if (sql.indexOf(",", sql.length() - 1) != -1) {
                sql.delete(sql.length() - 1, sql.length());
            }
        }
        return sql.toString();
    }


    private static String insert(MapperStatement mapperStatement, Object... param) throws Exception {
        Class<?> aClass = Class.forName(mapperStatement.getParameterType());
        String sql = mapperStatement.getSql();
        String id = mapperStatement.getId();
        StringBuffer bufferSql = new StringBuffer(sql.substring(0, sql.indexOf(")")));
        Element rootElement = mapperStatement.getElement();
        List<Element> list = rootElement.selectNodes("//if");
        Object o = param[0];
        boolean falg = true;
        for (Element element : list) {
            //是否是当前节点
            if (!isPresentNode(element, id)) {
                continue;
            }
            String test = element.attributeValue("test").trim();
            String name = test.split("\\!|=")[0];
            name = name.trim();
            Field declaredField = aClass.getDeclaredField(name);
            declaredField.setAccessible(true);
            Object para = declaredField.get(o);
            if (para != null) {
                String textTrim = element.getTextTrim();
                if (textTrim.indexOf("#{") == -1) {
                    bufferSql.append(" " + textTrim);
                } else {
                    if (falg) {
                        if (bufferSql.indexOf(",", bufferSql.length() - 1) != -1) {
                            bufferSql.delete(bufferSql.length() - 1, bufferSql.length());
                        }

                        bufferSql.append(" ) values ( ");
                        bufferSql.append(textTrim);
                        falg = false;
                    } else {
                        bufferSql.append(textTrim);
                    }
                }

            }
        }
        if (!falg) {
            if (bufferSql.indexOf(",", bufferSql.length() - 1) != -1) {
                bufferSql.delete(bufferSql.length() - 1, bufferSql.length());
            }
            bufferSql.append(" )");
            return bufferSql.toString();
        }
        return sql;

    }

    /**
     * 是否是当前节点
     *
     * @param element
     * @param id
     * @return
     */
    private static boolean isPresentNode(Element element, String id) {
        Element parent = element.getParent();
        String parenId = parent.attributeValue("id");
        if (!id.equals(parenId)) {
            Element parent01 = parent.getParent();
            String parenId01 = parent01.attributeValue("id");
            if (!id.equals(parenId01)) {
                return false;
            }
        }
        return true;
    }


    private static String select(MapperStatement mapperStatement, Object... param) throws Exception {
        String sql = mapperStatement.getSql().trim();
        Element rootElement = mapperStatement.getElement();
        String parameterTyp = mapperStatement.getParameterType();
        if(parameterTyp==null){
            return sql;
        }
        if (sql.endsWith("where")) {
            return dpAnalysis(mapperStatement.getId(), sql, null, parameterTyp, rootElement, param);
        }
        List<Element> list = rootElement.selectNodes("//where");
        boolean falg = false;
        for (Element element : list) {
            if (isPresentNode(element, mapperStatement.getId())) {
                falg = true;
            }
        }
        if (!falg) {
            return sql;
        }

        return dpAnalysis(mapperStatement.getId(), sql, "where", parameterTyp, rootElement, param);

    }

    private static String dpAnalysis(String id, String sql, String where, String parameterTyp, Element rootElement, Object... param) throws Exception {
        Class<?> aClass = Class.forName(parameterTyp);
        StringBuffer bufferSql = new StringBuffer(sql);

        List<Element> list = rootElement.selectNodes("//if");
        Object o = param[0];
        for (Element element : list) {
            //是否是当前节点
            if (!isPresentNode(element, id)) {
                continue;
            }
            String test = element.attributeValue("test").trim();
            String name = test.split("\\!|=")[0];
            name = name.trim();
            Field declaredField = aClass.getDeclaredField(name);
            declaredField.setAccessible(true);
            Object para = declaredField.get(o);
            if (para != null) {
                if (where != null) {
                    bufferSql.append(" " + where);
                    where=null;
                }
                String textTrim = element.getTextTrim();
                bufferSql.append(" " + textTrim);
            }
        }
        return deleteAnd(bufferSql);
    }

}
