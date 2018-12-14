package com.gdufe.study.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @Author: laichengfeng
 * @Description: Java执行CMD命令
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

    public static void main(String[] args) {
        String cmd = CMD("netsh wlan show hostednetwork");
        System.out.println(cmd);
    }
}
