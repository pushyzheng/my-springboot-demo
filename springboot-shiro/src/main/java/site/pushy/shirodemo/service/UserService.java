package site.pushy.shirodemo.service;

import site.pushy.shirodemo.bean.Role;
import site.pushy.shirodemo.bean.User;

import java.util.List;

/**
 * @author Pushy
 * @since 2018/11/20 21:56
 */
public interface UserService {

    User getUserById(String id);

    List<Role> listRoleByUserId(String id);

}
