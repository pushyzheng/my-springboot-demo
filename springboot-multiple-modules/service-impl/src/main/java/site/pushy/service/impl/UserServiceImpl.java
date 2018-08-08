package site.pushy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.pushy.bean.TbUser;
import site.pushy.dao.TbUserMapper;
import site.pushy.dao.UserDao;
import site.pushy.service.UserService;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private TbUserMapper userMapper;
    @Autowired
    private UserDao userDao;

    @Override
    public TbUser getUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
