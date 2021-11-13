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
public class SimpleExecutor  implements Executor{


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


        ResultSet resultSet = preparedStatement.executeQuery();

        Class<?> resultTypeClazz = Class.forName(mapperStatement.getResultType());
        List<Object> list = new ArrayList<>();
        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            Object o1 = resultTypeClazz.newInstance();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //获取列名
                String columnName = metaData.getColumnName(i);
                //去除下划线
                columnName = removeUnderline(columnName);
                //获取列值
                Object object = resultSet.getObject(columnName);
                Field declaredField = resultTypeClazz.getDeclaredField(columnName);
                declaredField.setAccessible(true);
                declaredField.set(o1, object);

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
        String s1 = s[0];
        for (int i = 1; i < s.length; i++) {
            String s2 = s[i];
            String substring = s2.substring(0, 1).toUpperCase();
            String substring1 = s2.substring(1);
            s1 = s1 + substring + substring1;
        }

        return s1;
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
