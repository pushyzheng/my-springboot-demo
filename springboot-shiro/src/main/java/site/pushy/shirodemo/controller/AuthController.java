package site.pushy.shirodemo.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.pushy.shirodemo.service.UserService;

/**
 * @author Pushy
 * @since 2018/11/20 11:33
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String login() {
        System.out.println(userService.getUserById("123").getRoles());
        return "login!";
    }

    @RequestMapping("/admin")
    @RequiresRoles(value = {"ROLE_ADMIN"}, logical = Logical.AND)
    public String admin() {
        return "admin";
    }
}
