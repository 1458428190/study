package com.gdufe.study.wifiretry.service;

import com.gdufe.study.wifiretry.utils.http.HttpUtils;
import com.gdufe.study.wifiretry.utils.http.NetState;
import org.apache.http.HttpException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: laichengfeng
 * @Description: 图书馆wifi重试服务
 * @Date: 2018/12/13 16:09
 */
public class WifiRetryService {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 学号
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * wifi名
     */
    private String wifiName;

    /**
     * wifi连接时间
     */
    private int wifiTime;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    public WifiRetryService(String user, String password, String wifiName, int wifiTime) {
        this.user = user;
        this.password = password;
        this.wifiName = wifiName;
        this.wifiTime = wifiTime;
    }

    /**
     * 尝试登陆黑科技
     */
    public void retryLogin() {
        System.out.println(simpleDateFormat.format(new Date()) + " 尝试登陆GDUFE黑科技");
        String url = "http://58.62.247.115/";
        Map<String, Object> params = new HashMap<>();
        params.put("DDDDD", user);
        params.put("upass", password);
        params.put("0MKKey", "%B5%C7%C2%BC+Login");
        params.put("Submit", "%E7%99%BB%E9%99%86");

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        headers.put("Host", "58.62.247.115");
        headers.put("Connection", "keep-alive");
        try {
            String post = HttpUtils.post(url, params, headers);
            if (post.contains("认证成功")) {
                System.out.println("登陆成功！");
            } else if(post.contains("信息页")) {
                System.out.println("学号或密码错误，请检查！");
            } else{
                System.out.println("登陆失败！请检查！");
            }
        } catch (Exception e) {
            System.out.println("登陆异常！请检查！");
        }
    }

    /**
     * 连接GDUFE wifi
     */
    public Boolean wifiConnect() {
        System.out.println(simpleDateFormat.format(new Date()) + " 尝试连接WIFI");
        String connWifiResult = NetState.connectWifi(wifiName, null);
        if(connWifiResult.contains("成功")) {
            System.out.println("连接成功！");
            return true;
        } else {
            System.out.println("连接失败！请检查wifi是否可连接");
            return false;
        }
    }

    /**
     * 判断是否已连接GDUFE相关wifi
     */
    public Boolean isConnectWifi() throws HttpException {
        return HttpUtils.get("http://58.62.247.115/", null) != null;
    }

    /**
     * 无网络时重连
     */
    public void retryGdufeWifi() throws HttpException, InterruptedException {
        // 无网
        boolean isConnect = NetState.isConnect();
        if(!isConnect) {
            System.out.println(simpleDateFormat.format(new Date()) + " --------shit, 没网，第"+ (++retryCount) + "次重连--------");
            long startTime = System.currentTimeMillis();
            boolean isConnectWifi = isConnectWifi();
            // 没有连接wifi
            if(!isConnectWifi) {
                System.out.println(simpleDateFormat.format(new Date()) + "----wifi已断开----");
                wifiConnect();
                Thread.sleep(wifiTime);
            }
            isConnectWifi = isConnectWifi();
            if(!isConnectWifi) {
                System.out.println("wifi还在开启中，请稍后~~~");
                Thread.sleep((wifiTime<<1) - (wifiTime>>1));
            }
            isConnect = NetState.isConnect();
            // 双重判断，减少网络请求
            if(isConnectWifi() && !isConnect) {
                retryLogin();
            }
            long endTime = System.currentTimeMillis();
            Thread.sleep(1000L);
            if(NetState.isConnect()) {
                System.out.println("----重连成功，耗时 " + (endTime - startTime) +"ms----\r\n");
            } else {
                System.out.println("--重连失败，耗时 " + (endTime - startTime) +"ms--\r\n");
            }
        }
    }

    public static void main(String[] args) {
        WifiRetryService wifiRetryService = new WifiRetryService("****","****","GDUFE", 2000);
        int t = 20;
        int sum = 0;
        while(t -- > 0) {
            long startTime = System.currentTimeMillis();
            System.out.println(wifiRetryService.wifiConnect());
            long endTime = System.currentTimeMillis();
            System.out.println("time-"+ t + " : "+ (endTime - startTime));
            sum += (endTime - startTime);
        }
        System.out.println(sum);
    }
}
