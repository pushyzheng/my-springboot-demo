package site.pushy.devtools.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping("/hello")
    public String hello(String name) {
        return "hello hello " + name;
    }
}