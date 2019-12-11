package com.hzaz.base.common_util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DimenWrite {

    /**
     * str:  要写入的文件内容  例如:{\"id\":1777944995971746430,\"frName\":\"会议纪要\",\"createDate\":\"2018-7-11\"}
     * path：   文件具体路径    例如:D:/111/2018/7/11/会议纪要.json
     * 配合 ScreenMatch 使用
     */
    public static void main(String[] args) {
        String path = "D:/dimens.xml";
        float rate = 0.5F;// TODO: 2019/12/11 这个值需要根据设计尺寸计算，https://blog.csdn.net/raoqian156/article/details/103496781
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file, true);
            for (int i = 1; i < 1080; i++) {
                String str = "<dimen name=\"padding_" + i + "px\">" + i * rate + "dp</dimen>";
                fw.write(str);//写入本地文件中
            }
            fw.flush();
            fw.close();
            System.out.println("执行完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
