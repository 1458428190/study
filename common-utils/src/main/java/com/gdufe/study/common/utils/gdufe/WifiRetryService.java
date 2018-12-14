package com.gdufe.study.common.utils.gdufe;

import com.gdufe.study.common.utils.http.NetState;
import org.apache.http.HttpException;

import java.util.HashMap;
import java.util.Map;

import static com.gdufe.study.common.utils.http.HttpUtils.post;

/**
 * @Author: laichengfeng
 * @Description: 图书馆wifi重试代码
 * @Date: 2018/12/13 16:09
 */
public class WifiRetryService {

    private static String WIFI_NAME = "GDUFE";

    private static long SLEEP_TIME = 10;

    /**
     * 尝试登陆黑科技
     * @throws HttpException
     */
    public void retryLogin() {
        System.out.println("尝试登陆GDUFE WIFI");
        String url = "http://58.62.247.115";
        Map<String, Object> params = new HashMap<>();
        params.put("DDDDD", "15251102120");
        params.put("upass", "311311311");
        params.put("0MKKey", "%25B5%25C7%25C2%25BC%2BLogin");
        params.put("Submit", "%E7%99%BB%E9%99%86");

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            String post = post(url, params, headers);
            if (post.contains("认证成功")) {
                System.out.println("成功！！！");
            } else {
                System.out.println("失败！！！请检查！！！！！");
            }
        } catch (Exception e) {
            System.out.println("失败！！！请检查！！！！");
        }
    }

    /**
     * 连接GDUFE wifi
     */
    public void wifiConnect() {
        System.out.println("尝试连接WIFI");
        String connWifiResult = NetState.connectWifi(WIFI_NAME, null);
        if(connWifiResult.contains("成功")) {
            System.out.println("成功！！！");
        } else {
            System.out.println("失败！！！请检查");
        }
    }

    /**
     * 无网络时重连
     */
    public void retryGdufeWifi() {
        // 无网
        if(!NetState.isConnect()) {
            wifiConnect();
            if(!NetState.isConnect()) {
                retryLogin();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        WifiRetryService wifiRetryService = new WifiRetryService();
        while(true) {
            wifiRetryService.retryGdufeWifi();
//            Thread.sleep(SLEEP_TIME);
        }
    }
}
