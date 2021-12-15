package com.hu.core.config.mapper;

import com.hu.core.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description: 根据resource解析mapper文件
 * @DateTime: 2021/10/30 3:25 下午
 **/
public class XmlMapperConfigBuilderByResource extends AbstractXmlMapperConfigBuilder {


    /**
     * 根据路径解析出每一个InputStream
     * @param path
     * @return
     */
    @Override
    public List<InputStream> doParsePathAsSteam(String path) throws IOException {
        return ResourceUtils.getStreamByPath(path);
    }
}
