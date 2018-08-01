package site.pushy.thymeleaf.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.pushy.thymeleaf.pojo.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class HelloController {

    @RequestMapping("")
    public String hello(Model model, @RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        model.addAttribute("name", name);
        User user = new User();
        user.setPassword("123");
        user.setUsername("Pushy");
        model.addAttribute("user", user);

        User user2 = new User();
        user2.setUsername("Lucy");
        List<User> userList = Arrays.asList(user, user2);
        model.addAttribute("userList", userList);

        return "hello";
    }

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, User user) {
        request.getSession().setAttribute("currentUser", user);
        request.getSession().setAttribute("role", "admin");
        return "index";
    }

    @GetMapping("/index")
    public String personal() {
        return "index";
    }
}
