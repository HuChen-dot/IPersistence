package com.hu.config.mapper;

/**
 * @Author: hu.chen
 * @Description: XmlMapperConfigBuilder工厂
 * @DateTime: 2021/10/30 5:04 下午
 **/
public class XmlMapperConfigBuilderFactory {

    public final static  String RESOURCE="resource";

    public final static  String URL="url";


    public static AbstractXmlMapperConfigBuilder getXmlMapperConfigBuilder(String type) throws Exception {
        switch (type){
            case RESOURCE:
                return new XmlMapperConfigBuilderByResource();
            case URL:
                return new XmlMapperConfigBuilderByUrl();
            default:
                throw new Exception("没找到具体的：XmlMapperConfigBuilder实现类");
        }
    }
}
