package com.hu.config.core;

import com.hu.bean.Configuration;
import com.hu.bean.MapperStatement;
import com.hu.config.mapper.AbstractXmlMapperConfigBuilder;
import com.hu.config.mapper.XmlMapperConfigBuilderFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: hu.chen
 * @Description: 核心配置文件解析
 * @DateTime: 2021/10/30 3:23 下午
 **/
public class XmlCoreConfigBuilder {

    private Configuration configuration;

    public XmlCoreConfigBuilder() {
        configuration = new Configuration();
    }

    /**
     * 解析inputstream成Configuration
     *
     * @param in
     * @return
     */
    public Configuration parseConfig(InputStream in) throws Exception {
        Document read = new SAXReader().read(in);

        //获取根节点
        Element rootElement = read.getRootElement();
        //获取dataSource节点
        List<Element> listproperty = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for (Element element : listproperty) {
            //获取当前属性节点的name属性的值
            String name = element.attributeValue("name");
            //获取当前属性节点的name属性的值
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        }
        //设置数据源
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("url"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));
        configuration.setDataSource(comboPooledDataSource);

        //获取mapperPath节点
        List<Element> listpath = rootElement.selectNodes("//path");
        if (listpath.size() == 0 || listpath.size() > 1) {
            throw new DocumentException("核心配置文件中path节点只能声明一个");
        }
        String type = XmlMapperConfigBuilderFactory.RESOURCE;
        Element pathElement = listpath.get(0);
        String path = pathElement.attributeValue(XmlMapperConfigBuilderFactory.RESOURCE);
        if (path == null) {
            path = pathElement.attributeValue(XmlMapperConfigBuilderFactory.URL);
            type = XmlMapperConfigBuilderFactory.URL;
        }
        if (path == null) {
            throw new Exception("mapper配置文件路径不能为空");
        }
        AbstractXmlMapperConfigBuilder xmlMapperConfigBuilder = XmlMapperConfigBuilderFactory.getXmlMapperConfigBuilder(type);
        Map<String, MapperStatement> mapperStatementMap = xmlMapperConfigBuilder.parseConfig(path);
        configuration.setMappers(mapperStatementMap);
        return configuration;
    }
}
