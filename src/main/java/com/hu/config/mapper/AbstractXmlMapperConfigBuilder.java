package com.hu.config.mapper;

import com.hu.bean.MapperStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: hu.chen
 * @Description: mapper解析顶层接口
 * @DateTime: 2021/10/30 3:26 下午
 **/
public abstract class AbstractXmlMapperConfigBuilder {


    /**
     * 根据路径解析成每一个MapperStatement
     *
     * @param path
     * @return
     */
    public Map<String, MapperStatement> parseConfig(String path) throws IOException, DocumentException {

        List<InputStream> ins = doParsePathAsSteam(path);

        Map<String, MapperStatement> mappers = new ConcurrentHashMap<>(16);
        for (InputStream in : ins) {
            Document read = new SAXReader().read(in);
            Element rootElement = read.getRootElement();
            String namespace = rootElement.attributeValue("namespace");
            List<Element> selectNodes = rootElement.selectNodes("//select");
            List<Element> updataNodes = rootElement.selectNodes("//update");
            List<Element> installNodes = rootElement.selectNodes("//insert");
            List<Element> deleteNodes = rootElement.selectNodes("//delete");
            //解析select
            doAnalysis(selectNodes,namespace, mappers);
            //解析update
            doAnalysis(updataNodes,namespace, mappers);
            //解析insert
            doAnalysis(installNodes,namespace, mappers);
            //解析delete
            doAnalysis(deleteNodes,namespace, mappers);
        }
        return mappers;
    }

    /**
     * 解析 sql相关内容 标签
     *
     * @param nodes
     * @param mappers
     * @param namespace
     */
    public void doAnalysis(List<Element>  nodes, String namespace, Map<String, MapperStatement> mappers) {
        for (Element element : nodes) {
            MapperStatement mapperStatement = new MapperStatement();
            //获取id
            String id = element.attributeValue("id");
            mapperStatement.setId(id);
            //获取返回值
            mapperStatement.setResultType(element.attributeValue("resultType"));
            //获取参数类型
            mapperStatement.setParameterTyp(element.attributeValue("parameterType"));
            String sql = element.getTextTrim();
            //获取sql语句
            mapperStatement.setSql(sql);
            //封装当前的节点对象
            mapperStatement.setElement(element);
            mappers.put(namespace + "." + id, mapperStatement);
        }

    }


    /**
     * 根据路径解析出每一个InputStream
     *
     * @param path
     * @return
     */
    public abstract List<InputStream> doParsePathAsSteam(String path) throws IOException;


}
