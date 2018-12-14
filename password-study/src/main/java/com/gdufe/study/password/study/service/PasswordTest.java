package com.gdufe.study.password.study.service;

import org.junit.Test;

import java.security.Provider;
import java.security.Security;
import java.util.Map;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2018/12/12 14:07
 */
public class PasswordTest {

    @Test
    public void outputProvider() {
        for (Provider p: Security.getProviders()) {
            System.out.println(p);

            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                System.out.println("\t" + entry.getKey());
            }
        }
    }
}
