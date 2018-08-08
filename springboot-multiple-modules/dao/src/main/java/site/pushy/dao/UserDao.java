package site.pushy.dao;

import org.springframework.stereotype.Component;

@Component
public class UserDao {

    public String getUser() {
        return "Pushy";
    }

}
