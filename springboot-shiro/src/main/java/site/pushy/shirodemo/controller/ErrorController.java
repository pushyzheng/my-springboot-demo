package site.pushy.shirodemo.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.pushy.shirodemo.bean.BaseResponse;

/**
 * @author Pushy
 * @since 2018/11/21 13:31
 */
@RestController
public class ErrorController {

    @RequestMapping(value = "/401", produces = "application/json")
    public String unauthorized() {
        BaseResponse response = new BaseResponse();
        response.setCode(401);
        response.setMessage("Unauthorized");
        response.setData(null);
        return JSON.toJSONString(response);
    }

}
