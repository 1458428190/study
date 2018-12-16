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
        // 由于后续要转成exe，方便使用和配置（故配置的读取换成了此方式）
        String propertiesPath = System.getProperty("user.dir") + "\\config.properties";
        FileInputStream fis = new FileInputStream(propertiesPath);
        properties.load(fis);
        System.out.println("config path:  " + propertiesPath);
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String retrySleepTime = properties.getProperty("retry_sleep_time");
        String wifiName = properties.getProperty("wifi_name");
        int wifiSpinTime = Integer.parseInt(properties.getProperty("wifi_spin_time"));
        int wifiSpinCount = Integer.parseInt(properties.getProperty("wifi_spin_count"));
        int successSleepTime = Integer.parseInt(properties.getProperty("success_sleep_time"));
        fis.close();
        System.out.println(user + " " + wifiName + " " + retrySleepTime + " " + wifiSpinTime + " "
                + wifiSpinCount + " " + successSleepTime + "\r\n");
        WifiRetryService wifiRetryService = new WifiRetryService(user, password, wifiName, wifiSpinTime, wifiSpinCount, successSleepTime);
        while(true) {
            wifiRetryService.retryGdufeWifi();
            Thread.sleep(Long.parseLong(retrySleepTime));
        }
    }
}

