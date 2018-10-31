package com.gdufe.study.xxljobdemo.jobhandler;

import org.springframework.stereotype.Component;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;

/**
 * @author: laichengfeng
 * @description:
 * @Date: 2018/10/31 15:05
 */
@JobHandler(value = "laichengfengJobHandler")
@Component
public class DemoJobHandler extends IJobHandler {

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("XXL-JOB, laichengfeng test");
        return SUCCESS;
    }
}