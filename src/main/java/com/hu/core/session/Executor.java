package com.hu.core.session;


import com.hu.core.bean.Configuration;
import com.hu.core.bean.MapperStatement;

import java.util.List;

/**
 * @Author: hu.chen
 * @Description: 执行器对象
 * @DateTime: 2021/10/30 8:56 下午
 **/
public interface Executor {

    /**
     * 执行查询
     *
     * @param configuration
     * @param mapperStatement
     * @param param
     * @param <T>
     * @return
     */
    <T> List<T> queue(Configuration configuration, MapperStatement mapperStatement, Object... param) throws Exception;


    /**
     * 执行更新（增加，删除，修改）
     *
     * @param configuration
     * @param mapperStatement
     * @param param
     * @return
     */
    int change(Configuration configuration, MapperStatement mapperStatement, Object... param) throws Exception;


}
