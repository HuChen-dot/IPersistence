package com.hu.config.sql;

import com.hu.bean.MapperStatement;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description: sql+if标签解析
 * @DateTime: 2021/11/13 11:47 上午
 **/
public class SqlIfAnalysis {

    public static final String WHERE="where";
    public static final String SELECT="select";
    public static final String INSERT="insert";
    public static final String DELETE="delete";


    /**
     * if标签解析
     *
     * @param mapperStatement sql相关参数的封装类
     * @param param           参数
     * @return
     * @throws Exception
     */
    public static String analysis(MapperStatement mapperStatement, Object... param) throws Exception {
        //获得sql
        String sql = mapperStatement.getSql().trim().toLowerCase();
        String parameterTyp = mapperStatement.getParameterType();
        //参数类型为空则直接返回sql
        if (parameterTyp == null) {
            return sql;
        }
        //获取当前节点
        Element rootElement = mapperStatement.getElement();
        //获取参数类class
        Class<?> aClass = Class.forName(mapperStatement.getParameterType());
        //获取参数值
        Object o = param[0];
        //获取当前节点的if标签
        List<Element> ifs = rootElement.selectNodes(".//if");

        if (sql.startsWith(SELECT) || sql.startsWith(DELETE)) {
            return analysisWhereRear(sql, ifs, aClass, o);
        }
        if (sql.startsWith(INSERT)) {
            return insertIfRear(sql, ifs, aClass, o);
        }
        return updateIfAnalysis(sql, ifs, aClass, o);
    }

    /**
     * 解析update语句
     *
     * @return
     */
    private static String updateIfAnalysis(String sql, List<Element> ifs, Class<?> aClass, Object o) throws Exception {
        //先去除where
        if (sql.endsWith(WHERE)) {
            sql = sql.substring(0, sql.indexOf(WHERE));
        }
        StringBuffer bufferIf = new StringBuffer(sql);
        List<Element> ifsWhere = new ArrayList<>();
        for (Element element : ifs) {
            Object para = getObj(element, aClass, o);
            if (para != null) {
                //获取if标签中的文本
                String textTrim = element.getTextTrim();
                if (textTrim.endsWith(",")) {
                    bufferIf.append(" ").append(textTrim).append(" ");
                } else {
                    ifsWhere.add(element);
                }
            }
        }
        sql = delCommaRear(bufferIf.toString());
        return analysisWhereRear(sql, ifsWhere, aClass, o);
    }


    /**
     * 解析insert语句if标签
     *
     * @param sql    sql语句
     * @param ifs    当前if节点集合
     * @param aClass 参数类型
     * @param o      参数对象
     * @return
     * @throws Exception
     */
    private static String insertIfRear(String sql, List<Element> ifs, Class<?> aClass, Object o) throws Exception {
        StringBuffer bufferIf = new StringBuffer();
        sql = sql.substring(0, sql.indexOf("(") + 1);
        boolean flag = true;
        for (Element element : ifs) {
            Object para = getObj(element, aClass, o);
            if (para != null) {
                //获取if标签中的文本
                String textTrim = element.getTextTrim();
                //代表是values上面的
                if (textTrim.indexOf("#{") == -1) {
                    bufferIf.append(textTrim).append(" ");
                } else {
                    //代表是values下面的
                    if (flag) {
                        bufferIf.delete(bufferIf.length() - 1, bufferIf.length());
                        bufferIf = new StringBuffer(delCommaRear(bufferIf.toString()));
                        bufferIf.append(" ) values ( ");
                        bufferIf.append(textTrim);
                        flag = false;
                        continue;
                    }
                    bufferIf.append(textTrim).append(" ");
                }
            }
        }
        return sql + delCommaRear(bufferIf.toString()) + ")";
    }


    /**
     * 解析where后面的if标签
     *
     * @param sql    sql语句
     * @param ifs    当前if节点集合
     * @param aClass 参数类型
     * @param o      参数对象
     * @return
     * @throws Exception
     */
    private static String analysisWhereRear(String sql, List<Element> ifs, Class<?> aClass, Object o) throws Exception {
        StringBuffer bufferIf = new StringBuffer();
        for (Element element : ifs) {
            Object para = getObj(element, aClass, o);
            if (para != null) {
                bufferIf.append(element.getTextTrim()).append(" ");
            }
        }
        if (bufferIf.length() != 0) {
            if (sql.endsWith(WHERE)) {
                sql = sql + " " + delAnd(bufferIf.toString());
            } else {
                sql = sql + " "+ WHERE +" " + delAnd(bufferIf.toString());
            }
        } else {
            if (sql.endsWith(WHERE)) {
                return sql.substring(0, sql.indexOf(WHERE));
            }
        }
        return sql;
    }

    /**
     * 获取字段属性值
     *
     * @param element
     * @param aClass
     * @param o
     * @return
     * @throws Exception
     */
    private static Object getObj(Element element, Class<?> aClass, Object o) throws Exception {
        //获取if标签中test属性
        String test = element.attributeValue("test").trim();
        //获取test条件中的字段名
        String name = test.split("\\!|=")[0].trim();
        //根据test中获取的字段名从参数类中获取当前字段
        Field declaredField = aClass.getDeclaredField(name);
        //开启暴力反射
        declaredField.setAccessible(true);
        return declaredField.get(o);
    }

    /**
     * 去除字符串最后的逗号
     *
     * @param sql
     * @return
     */
    private static String delCommaRear(String sql) {
        sql = sql.trim();
        if (!sql.endsWith(",")) {
            return sql;
        }
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * 删除字符串中前缀and
     *
     * @param sql
     * @return
     */
    private static String delAnd(String sql) {
        sql = sql.trim();
        if (!sql.startsWith("and")) {
            return sql;
        }
        return sql.substring(3);
    }


}
