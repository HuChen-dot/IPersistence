package com.hu.session;

import com.hu.config.sql.SqlIfAnalysis;
import com.hu.util.ParameterMappingTokenHandler;
import com.hu.bean.BoundSql;
import com.hu.bean.Configuration;
import com.hu.bean.MapperStatement;
import com.hu.bean.ParameterMapping;
import com.hu.util.GenericTokenParser;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description: 执行器对象实现类
 * @DateTime: 2021/10/30 8:57 下午
 **/
public class SimpleExecutor implements Executor {


    /**
     * 执行查询
     *
     * @param configuration
     * @param mapperStatement
     * @param param
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> queue(Configuration configuration, MapperStatement mapperStatement, Object... param) throws Exception {
        //获取预编译对象
        PreparedStatement preparedStatement = getPreparedStatement(configuration, mapperStatement, param);

        List<String> columns = unSql(mapperStatement.getSql());


        ResultSet resultSet = preparedStatement.executeQuery();
        //获取返回值类型
        Class<?> resultTypeClazz = Class.forName(mapperStatement.getResultType());
        List<Object> list = new ArrayList<>();
        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            //实例化一个返回值对象，
            Object o1 = resultTypeClazz.newInstance();
            if (columns.size() == 1 && columns.get(0).equals("*")) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    //获取列名
                    String columnName = metaData.getColumnName(i);
                    setField( resultSet,columnName,resultTypeClazz, o1);

                }
            }else {
                for (String column : columns) {
                    setField( resultSet,column,resultTypeClazz, o1);
                }
            }

            list.add(o1);
        }
        return (List<T>) list;
    }




    /**
     * 执行更新（增加，删除，修改）
     *
     * @param configuration
     * @param mapperStatement
     * @param param
     * @return
     */
    @Override
    public int change(Configuration configuration, MapperStatement mapperStatement, Object... param) throws Exception {
        //获取预编译对象
        PreparedStatement preparedStatement = getPreparedStatement(configuration, mapperStatement, param);
        return preparedStatement.executeUpdate();
    }


    /**
     * 获取预编译对象
     *
     * @param configuration
     * @param mapperStatement
     * @param param
     * @return
     * @throws Exception
     */
    private PreparedStatement getPreparedStatement(Configuration configuration, MapperStatement mapperStatement, Object... param) throws Exception {
        //获取Connection
        Connection connection = configuration.getDataSource().getConnection();

        //解析<if>条件
        String sql = SqlIfAnalysis.analysis(mapperStatement, param);

//        select * from user where id=#{id}
//
//        select * from user where id=?
        System.err.println("解析前的sql:-->" + sql);
        //解析占位符
        BoundSql boundSql = getBoundSql(sql);
        String sql1 = boundSql.getSql();
        System.err.println("解析后的sql:-->" + sql1);
        PreparedStatement preparedStatement = connection.prepareStatement(sql1);

        //设置参数
        if (sql1.indexOf("?") != -1) {
            List<ParameterMapping> parameterMappingist = boundSql.getParameterMappingist();
            Class<?> parameterTypeClass = Class.forName(mapperStatement.getParameterType());
            Object o = param[0];
            //参数拼接
            StringBuffer paramLog = new StringBuffer();
            for (int i = 0; i < parameterMappingist.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingist.get(i);
                String content = parameterMapping.getContent();
                Field declaredField = parameterTypeClass.getDeclaredField(content);
                declaredField.setAccessible(true);
                Object o1 = declaredField.get(o);
                int j = i + 1;
                preparedStatement.setObject(j, o1);
                paramLog.append("参数" + j + "：").append(o1).append(" ");
            }
            System.err.println(paramLog.toString());
        }
        return preparedStatement;
    }


    /**
     * 将列名中的下划线转成驼峰
     *
     * @param columnName
     * @return
     */
    private static String removeUnderline(String columnName) {
        if (columnName.indexOf("_") == -1) {
            return columnName;
        }
        String[] s = columnName.split("_");
        StringBuffer column = new StringBuffer(s[0]);
        for (int i = 1; i < s.length; i++) {
            String s2 = s[i];
            char c = s2.charAt(0);
            if (c < 97) {
                column.append(s2);
                continue;
            }
            c = (char) (c - 32);
            column.append(c).append(s2.substring(1));
        }
        return column.toString();
    }

    /**
     * 解析出想要查询的列
     * @param sql
     * @return
     */
    private  List<String> unSql(String sql) {
        sql = sql.trim();
        sql = sql.substring(6, sql.indexOf("from"));
        List<String> comm = new ArrayList<>();
        if (sql.indexOf("*") != -1) {
            comm.add("*");
            return comm;
        }
        String[] split = sql.split(",");
        for (int i = 0; i < split.length; i++) {
            String s = split[i].trim();
            String s1 = s.toLowerCase();
            if (s1.indexOf(" as ") == -1) {
                comm.add(s);
                continue;
            }
            String s3 = s.split(" ")[2];
            s3 = s3.replace("\"", "");
            comm.add(s3.trim());
        }

        return comm;
    }

    /**
     * 给对象设置值
     * @param resultSet
     * @param column
     * @param resultTypeClazz
     * @param o1
     * @throws Exception
     */
    private void setField(ResultSet resultSet,String column,Class<?> resultTypeClazz,Object o1) throws Exception {
        //获取列值
        Object object = resultSet.getObject(column);
        //去除下划线
        column = removeUnderline(column);
        Field declaredField = resultTypeClazz.getDeclaredField(column);
        declaredField.setAccessible(true);
        declaredField.set(o1, object);
    }

    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String parse = parser.parse(sql);
        List<ParameterMapping> parameterMappings = handler.getParameterMappings();
        BoundSql boundSql = new BoundSql(parse, parameterMappings);
        return boundSql;
    }
}
