package site.pushy.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.pushy.service.UserService;
import site.pushy.util.RespEntity;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/users/{id}")
    public String getUserById(@PathVariable String id) {
        return RespEntity.success(userService.getUserById(id));
    }
}
