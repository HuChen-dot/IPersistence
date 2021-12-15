package com.hu.core.agency;


import com.hu.core.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/10/31 1:07 下午
 **/
public class MapperAgency implements InvocationHandler {

    private SqlSession sqlSession;

    public MapperAgency(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //方法名
        String name = method.getName();
        //类的全限定名
        String classPath = method.getDeclaringClass().getName();

        Type genericReturnType = method.getGenericReturnType();
        String typeName = genericReturnType.getTypeName();
//        java.util.list
        return sqlSession.doExecutor(typeName, classPath + "." + name, args);
    }

    public <T> T getObj(Class<T> clazz) {
        //第一个参数：当前运行类的类加载器
        //第二个参数：需要动态代理的接口（jdk动态代理，需要有接口的支持）
        //第三个参数：自定义的实现了InvocationHandler 接口的实现类；
        T o = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        //返回生成后的代理对象
        return o;
    }
}
