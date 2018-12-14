package com.gdufe.study.wifiretry.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @Author: laichengfeng
 * @Description: 执行CMD命令工具
 * @Date: 2018/12/13 15:50
 */
public class CmdUtils {

    public static Runtime runtime = Runtime.getRuntime();

    /**
     * 执行特定命令，比如 ping www.baidu.com
     * @param command
     * @return
     * @throws IOException
     */
    public static String CMD(String command) {
        try {
            Process process = runtime.exec(command);
            // cmd命令执行结果是GBK编码
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
            StringBuilder sb = new StringBuilder();
            String buffer;
            while ((buffer = br.readLine()) != null) {
                sb.append(buffer + "\r\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            // TODO 异常处理
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        String property = System.getProperty("user.name");
        System.out.println(property);
        properties.load(new FileInputStream("C:\\Users\\{username}\\Desktop\\wifi-retry\\application.properties"));
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String sleepTime = properties.getProperty("sleep_time");
        String wifiName = properties.getProperty("wifi_name");
        System.out.println(user + "  " + password + " " + wifiName + "" + sleepTime);
    }
}
