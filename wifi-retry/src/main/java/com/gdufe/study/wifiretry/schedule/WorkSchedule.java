package com.gdufe.study.wifiretry.schedule;

import com.gdufe.study.wifiretry.service.WifiRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Author: laichengfeng
 * @Description: 定时任务，已舍弃
 * @Date: 2018/12/13 20:25
 */
//@Component
@Deprecated
public class WorkSchedule {

    @Autowired
    private WifiRetryService wifiRetryService;

    @Scheduled(cron = "* * * * * ?")
    public void connectWifi() {
//        wifiRetryService.retryGdufeWifi();
    }
}