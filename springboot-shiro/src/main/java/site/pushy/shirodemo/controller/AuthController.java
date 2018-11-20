package site.pushy.shirodemo.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pushy
 * @since 2018/11/20 11:33
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @RequestMapping("/login")
    public String login() {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("Pushy", "123");
        return null;
    }

}
