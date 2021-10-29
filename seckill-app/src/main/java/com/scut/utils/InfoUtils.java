package com.scut.utils;

import com.scut.vo.BuyInformation;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 用于生成压测时多用户测试需要的配置文件
 */
public class InfoUtils {
    private static void createBuyInfo(int count) throws Exception{
        List<BuyInformation> list = new ArrayList<>();
        Random random = new Random();
        for(int i = 1;i <= count;i++){
            BuyInformation buyInformation = new BuyInformation();
            buyInformation.setUserId(i);
            //buyInformation.setProductId(random.nextInt(3) + 1);
            buyInformation.setProductId(1);
            list.add(buyInformation);
        }
        System.out.println("create buyInformation");
        File file = new File("C:\\Users\\Administrator\\Desktop\\userConf.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i = 0;i < list.size();i++) {
            BuyInformation buyInformation = list.get(i);
            String row = buyInformation.getUserId() + "," + buyInformation.getProductId();
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
        }
        raf.close();
        System.out.println("over");
    }

    public static void main(String[] args) throws Exception {
        createBuyInfo(5000);
    }
}
