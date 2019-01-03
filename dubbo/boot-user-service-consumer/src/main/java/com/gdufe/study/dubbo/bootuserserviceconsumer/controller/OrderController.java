package com.gdufe.study.dubbo.bootuserserviceconsumer.controller;

import com.gdufe.study.dubbo.bean.UserAddress;
import com.gdufe.study.dubbo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2019/1/3 13:36
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/initOrder")
    public List<UserAddress> initOrder(@RequestParam("uid")String userId) {
        return orderService.initOrder(userId);
    }
}
