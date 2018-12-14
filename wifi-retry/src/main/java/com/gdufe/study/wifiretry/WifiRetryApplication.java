package com.gdufe.study.wifiretry;

import com.gdufe.study.wifiretry.service.WifiRetryService;
import org.apache.http.HttpException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class WifiRetryApplication {

    public static void main(String[] args) throws IOException, InterruptedException, HttpException {
        System.out.println("\r\n\r\n\r\n------------------------Code written by laichengfeng------------------------\r\n\r\n\r\n");
        SpringApplication.run(WifiRetryApplication.class, args);
        Properties properties = new Properties();
//        String filePath = "C:\\Users\\{username}\\Desktop\\wifi-retry\\application.properties";
        // 由于后续要转成exe，方便使用和配置（故配置的读取换成了此方式）
        String filePath = args[0];
        String propertiesPath = filePath.replace("{username}", System.getProperty("user.name"));
        FileInputStream fis = new FileInputStream(propertiesPath);
        properties.load(fis);
        System.out.println("config path:  " + propertiesPath);
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String sleepTime = properties.getProperty("sleep_time");
        String wifiName = properties.getProperty("wifi_name");
        int wifiTime = Integer.parseInt(properties.getProperty("wifi_time"));
        fis.close();
        System.out.println(user + " " + wifiName + " " + sleepTime + " " + wifiTime + "\r\n");
        WifiRetryService wifiRetryService = new WifiRetryService(user, password, wifiName, wifiTime);
        while(true) {
            wifiRetryService.retryGdufeWifi();
            Thread.sleep(Long.parseLong(sleepTime));
        }
    }
}

