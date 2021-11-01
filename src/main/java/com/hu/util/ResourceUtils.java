package com.hu.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description: 读取配置文件工具类
 * @DateTime: 2021/10/30 2:17 下午
 **/
public class ResourceUtils {
    private final static String ROOT_PATH = "target/classes";

    public  final static String SEPARATOR = "/";

    public static List<InputStream> getStreamByPath(String path) throws IOException {
        String item = null;
        if (path.startsWith(SEPARATOR)) {
            item = ROOT_PATH + path;
        } else {
            path = SEPARATOR + path;
            item = ROOT_PATH + SEPARATOR + path;
        }
        File file = new File(item);
        if (!file.exists()) {
            throw new IOException("找不到指定的文件,请查看路径是否正确!!!");
        }
        List<InputStream> ins = new ArrayList<>();
        //如果路径是文件夹，则加载此文件夹下的文件
        if (file.isDirectory()) {
            //获取该文件夹下所有的文件
            File[] fileArray = file.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                String itemPath = null;
                if (path.endsWith(SEPARATOR)) {
                    itemPath = path + fileArray[i].getName();
                } else {
                    itemPath = path + SEPARATOR + fileArray[i].getName();
                }
                InputStream is = ResourceUtils.class.getResourceAsStream(itemPath);
                ins.add(is);
            }
            return ins;
        }

        InputStream is = ResourceUtils.class.getResourceAsStream(path);
        ins.add(is);
        return ins;
    }


}
