package com.hu.io;

import com.hu.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description: 资源加载类
 * @DateTime: 2021/10/30 1:46 下午
 **/
public class IResources {


    /**
     * 加载配置文件
     * @param path
     * @return
     * @throws Exception
     */
    public static InputStream getResourceAsStream(String path) throws IOException {
        List<InputStream> streamByPath = ResourceUtils.getStreamByPath(path);
        if(streamByPath.size()>1){
            throw new IOException("根据路径找到多个文件，请指定具体的核心配置文件!!!");
        }
        return streamByPath.get(0);
    }



}
