package site.pushy.shirodemo.service;

import org.springframework.stereotype.Service;
import site.pushy.shirodemo.bean.Role;
import site.pushy.shirodemo.bean.User;

import java.util.List;

/**
 * @author Pushy
 * @since 2018/11/20 21:57
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getUserById(String id) {
        return null;
    }

    @Override
    public List<Role> listRoleByUserId(String id) {
        return null;
    }
}
