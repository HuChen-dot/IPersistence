package com.hu.sqlsession;

import com.hu.agency.MapperAgency;
import com.hu.bean.Configuration;
import com.hu.bean.MapperStatement;

import java.util.List;

/**
 * @Author: hu.chen
 * @Description: sqlSession默认实现类
 * @DateTime: 2021/10/30 8:00 下午
 **/
public class DefaultSqlSession implements SqlSession{

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 查询单个
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> T selectOne(String statemenId, Object... param) throws Exception {
        List<T> objects = selectList(statemenId, param);
        if(objects.size()>1){
            throw new RuntimeException("返回结果过多，该方法只能返回1条记录");
        }
        if(objects.size()==1){
            return objects.get(0);
        }
        return null;
    }

    /**
     * 查询多个
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> selectList(String statemenId, Object... param) throws Exception {
        return (List<T>) executor("List",statemenId,  param);
    }

    /**
     * 修改
     *
     * @param statemenId
     * @param param
     * @return
     */
    @Override
    public int update(String statemenId, Object... param) throws Exception {
        return (int) executor("Integer",statemenId,  param);
    }

    /**
     * 添加
     *
     * @param statemenId
     * @param param
     * @return
     */
    @Override
    public int insert(String statemenId, Object... param) throws Exception {
        return (int) executor("Integer",statemenId,  param);
    }

    /**
     * 删除
     *
     * @param statemenId
     * @param param
     * @return
     */
    @Override
    public int delete(String statemenId, Object... param) throws Exception {
        return (int) executor("Integer",statemenId,  param);
    }


    @Override
    public Object executor(String typeName,String statemenId, Object... param) throws Exception {
        MapperStatement mapperStatement = configuration.getMappers().get(statemenId);
        if(mapperStatement==null){
            throw new RuntimeException("找不到对应的sql请核对路径");
        }
        Executor executor=new SimpleExecutor();
        if(typeName.indexOf("List")!=-1){
            return executor.queue(configuration,mapperStatement,param);
        }
        String sql = mapperStatement.getSql().trim().toLowerCase();
        if (sql.startsWith("select")) {
           return selectOne(statemenId,param);
        }
        return executor.change(configuration,mapperStatement,param);
    }

    /**
     * 获取代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getMapper(Class<T> clazz){
        return new MapperAgency(this).getObj(clazz);
    }


}
