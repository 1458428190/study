package com.gdufe.study.sofaboot.service.consumer;

import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.gdufe.study.sofaboot.service.facade.SampleJvmService;

/**
 * @author: laichengfeng
 * @description: JVM引用服务, 注解方式
 * @Date: 2018/10/10 15:24
 */
public class JvmServiceConsumer implements ClientFactoryAware {

    private ClientFactory clientFactory;

    /**
     * XML引用
     */
//    @Autowired
    private SampleJvmService sampleJvmService;

    /**
     * 注解引用
     */
//    @SofaReference(uniqueId = "annotationImpl")
    private SampleJvmService sampleJvmServiceAnnotation;

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void init() {
        System.out.println("-------------Test SOFABOOT---------------------");
        sampleJvmService.message();
        sampleJvmServiceAnnotation.message();

        // API 引用方式
        ReferenceClient referenceClient = clientFactory.getClient(ReferenceClient.class);
        ReferenceParam<SampleJvmService> referenceParam = new ReferenceParam<SampleJvmService>();
        referenceParam.setInterfaceType(SampleJvmService.class);
        referenceParam.setUniqueId("annotationImpl");
        SampleJvmService sampleJvmService = referenceClient.reference(referenceParam);
        sampleJvmService.message();
    }
}