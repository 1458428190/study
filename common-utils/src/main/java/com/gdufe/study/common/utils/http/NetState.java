package com.gdufe.study.common.utils.http;

import com.gdufe.study.common.utils.CmdUtils;
import org.apache.commons.lang.StringUtils;

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
    public static String connectWifi(String wifiName, String ssid) {
        String command = "netsh wlan connect name=" + wifiName;
        if(StringUtils.isNotBlank(ssid)) {
            command += " ssid=" + ssid;
        }
        return CmdUtils.CMD(command);
    }

    public static boolean isConnect(){
        boolean connect = false;
        String sb = CmdUtils.CMD("ping www.baidu.com");
        if (StringUtils.isNotBlank(sb)) {
            if (sb.indexOf("TTL") > 0) {
                // 网络畅通
                connect = true;
            } else {
                // 网络不畅通
                connect = false;
            }
        }
        return connect;
    }

    public static void main(String[] args) {
//        System.out.println(NetState.isConnect());
        String gdufe = NetState.connectWifi("GDUFE", null);
        System.out.println(gdufe);
    }
}
