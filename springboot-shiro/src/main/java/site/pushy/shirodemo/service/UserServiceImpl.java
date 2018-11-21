package site.pushy.shirodemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.pushy.shirodemo.bean.Role;
import site.pushy.shirodemo.bean.User;
import site.pushy.shirodemo.repository.RoleRepository;
import site.pushy.shirodemo.repository.UserRepository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Pushy
 * @since 2018/11/20 21:57
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private RoleRepository roleRepository;

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    @Override
    public List<Role> listRoleByUserId(String id) {
        return roleRepository.findByUserId(id);
    }

}
