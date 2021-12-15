package com.hu.core.config.mapper;



import com.hu.core.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description: 根据url解析配置文件
 * @DateTime: 2021/10/30 3:24 下午
 **/
public class XmlMapperConfigBuilderByUrl extends AbstractXmlMapperConfigBuilder {


    /**
     * 根据路径解析出每一个InputStream
     * @param path
     * @return
     */
    @Override
    public List<InputStream> doParsePathAsSteam(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("找不到指定的文件,请查看路径是否正确!!!");
        }
        List<InputStream> ins = new ArrayList<>();
        if (file.isDirectory()) {
            //获取该文件夹下所有的文件
            File[] fileArray = file.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                String name = fileArray[i].getName();
                if(!name.endsWith("Mapper.xml")){
                    continue;
                }

                String itemPath = null;
                if (path.endsWith(ResourceUtils.SEPARATOR)) {
                    itemPath = path + fileArray[i].getName();
                } else {
                    itemPath = path + ResourceUtils.SEPARATOR + fileArray[i].getName();
                }
                BufferedInputStream isinput = new BufferedInputStream(new FileInputStream( new File(itemPath)));
                ins.add(isinput);
            }
            return ins;
        }

        BufferedInputStream isinput = new BufferedInputStream(new FileInputStream(file));
        ins.add(isinput);
        return ins;
    }
}
