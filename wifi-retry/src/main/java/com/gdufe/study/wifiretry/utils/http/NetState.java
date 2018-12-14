package com.gdufe.study.wifiretry.utils.http;

import com.gdufe.study.wifiretry.utils.CmdUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2018/12/13 15:47
 */
public class NetState {

    /**
     * 打开wifi, 需要管理员权限
     * @return
     */
    public static String openWifi() {
        return CmdUtils.CMD("netsh wlan start hostednetwork");
    }

    /**
     * 连接指定wifi
     * @param wifiName
     *      wifi名
     * @param ssid
     *      密码
     * @return
     */
    public static String connectWifi(String wifiName, String key) {
            String command = "netsh wlan connect name=" + wifiName;
        if(StringUtils.isNotBlank(key)) {
            command += " key=" + key;
        }
        return CmdUtils.CMD(command);
    }

    /**
     * 断开wifi连接
     * @return
     */
    public static String disconnectWifi() {
        String command = "netsh wlan disconnect";
        return CmdUtils.CMD(command);
    }

    public static boolean isConnect(){
          // 速度太慢，已弃用 （平均每次判断需要3秒以上）
//        String sb = CmdUtils.CMD("ping www.baidu.com");
//        if (StringUtils.isNotBlank(sb)) {
//            if (sb.indexOf("TTL") > 0) {
//                // 网络畅通
//                connect = true;
//            } else {
//                // 网络不畅通
//                connect = false;
//            }
//        }
        // 毫秒级判断
        try {
            String content = HttpUtils.get("http://www.baidu.com", null);
            return StringUtils.contains(content, "百度");
        } catch (HttpException e) {
            return false;
        }
    }

    public static void main(String[] args)  {
////        System.out.println(NetState.isConnect());
//        int t = 1;
////        while(t-- > 0) {
//            String gdufe = NetState.connectWifi("GDUFE", null);
//            System.out.println(gdufe);
////        }
        int t = 20;
        int sum = 0;
        while(t -- > 0) {
            long startTime = System.currentTimeMillis();
            System.out.println(isConnect());
            long endTime = System.currentTimeMillis();
            System.out.println("time-"+ t + " : "+ (endTime - startTime));
            sum += (endTime - startTime);
        }
        System.out.println(sum);
    }
}
