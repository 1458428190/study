/**
 * @(#)PublishServiceWithClient.java, 2018/10/10.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gdufe.study.sofaboot.service.provider;

import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.gdufe.study.sofaboot.service.facade.SampleJvmService;

/**
 * @author: laichengfeng (laichengfeng @ corp.netease.com)
 * @description: JVM发布服务, API方式
 * @Date: 2018/10/10 15:09
 */
public class PublishServiceWithClient implements ClientFactoryAware {

    private ClientFactory clientFactory;

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void init() {
        ServiceClient serviceClient = clientFactory.getClient(ServiceClient.class);
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new SampleJvmServiceImpl(
                "Hello, laichengfeng, jvm service service client implementation."));
        serviceParam.setInterfaceType(SampleJvmService.class);
        serviceParam.setUniqueId("serviceClientImpl");
        serviceClient.service(serviceParam);
    }
}