package org.spring.springboot.web;

import com.alibaba.dubbo.config.annotation.Reference;
import org.spring.springboot.domain.City;
import org.spring.springboot.dubbo.CityDubboService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CityController {

    @Reference(version = "1.0.0")
    CityDubboService cityDubboService;

    @RequestMapping("/city")
    public String printCity() {
        String cityName="南平";
        City city = cityDubboService.findCityByName(cityName);
        return city.toString();
    }

}
