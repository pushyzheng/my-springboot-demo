package site.pushy.shirodemo.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.pushy.shirodemo.service.UserService;
import site.pushy.shirodemo.util.JWTUtil;

/**
 * @author Pushy
 * @since 2018/11/20 11:33
 */
@RestController
public class AuthController {

    /**
     * 生成Token
     */
    @RequestMapping("/login")
    public String login() {
        return JWTUtil.encode("123");
    }

    @RequestMapping("/users")
    public String users() {
        return "ok";
    }

    @RequestMapping("/admin")
    @RequiresRoles(value = {"ROLE_ADMIN"}, logical = Logical.AND)
    public String admin() {
        return "admin";
    }
}
