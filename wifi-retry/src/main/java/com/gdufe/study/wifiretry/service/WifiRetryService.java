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
     * wifi连接自旋等待时间
     */
    private int wifiSpinTime;

    /**
     * wifi连接自旋次数
     */
    private int wifiSpinCount;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    /**
     * 登陆成功后休息时间
     */
    private int successSleepTime;

    public WifiRetryService(String user, String password, String wifiName, int wifiSpinTime, int wifiSpinCount, int successSleepTime) {
        this.user = user;
        this.password = password;
        this.wifiName = wifiName;
        this.wifiSpinTime = wifiSpinTime;
        this.successSleepTime = successSleepTime;
        this.wifiSpinCount = wifiSpinCount;
    }

    /**
     * 尝试登陆黑科技
     */
    public boolean retryLogin() {
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
                return true;
            } else if(post.contains("信息页")) {
                System.out.println("学号或密码错误，请检查！");
            } else{
                System.out.println("登陆失败！请检查！");
            }
        } catch (Exception e) {
            System.out.println("登陆异常！请检查！");
        }
        return false;
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
            boolean isRetrySuccess = true;
            // 没有连接wifi
            if(!isConnectWifi) {
                System.out.println(simpleDateFormat.format(new Date()) + "----wifi已断开----");
                if(!wifiConnect()) {
                    return;
                }
            }
            // 自旋锁等待
            int spinCount = 0;
            while(!isConnectWifi()) {
                if((++spinCount) > wifiSpinCount) {
                    System.out.println("----wifi连接已超过自旋次数，仍未连接到wifi，稍后自动重试----");
                    return;
                }
                Thread.sleep(wifiSpinTime);
            }
            if(!NetState.isConnect()) {
                isRetrySuccess = retryLogin();
            }
            long endTime = System.currentTimeMillis();
            if(isRetrySuccess) {
                System.out.println("----重连成功，耗时 " + (endTime - startTime) +"ms----\r\n");
                Thread.sleep(successSleepTime);
            } else if(!NetState.isConnect()){
                System.out.println("--重连失败，耗时 " + (endTime - startTime) +"ms--\r\n");
            }
        }
    }

    public static void main(String[] args) {
        WifiRetryService wifiRetryService = new WifiRetryService("****","****",
                "GDUFE", 2000, 2000, 2000);
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
