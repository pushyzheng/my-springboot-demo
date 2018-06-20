package com.pushy.servicefeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

// fallback指定了熔断器触发的类
@FeignClient(value = "service-hi",fallback = SchedualServiceHiHystric.class)
public interface SchedualServiceHi {

    /**
     * 定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。这里调用了service-hi服务的“/hi”接口
     * @param name
     * @return
     */
    @RequestMapping(value = "hi", method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
