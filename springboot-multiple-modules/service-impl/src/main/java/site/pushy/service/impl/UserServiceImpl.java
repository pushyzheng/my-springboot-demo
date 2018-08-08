package site.pushy.service.impl;

import org.springframework.stereotype.Service;
import site.pushy.bean.TbUser;
import site.pushy.dao.TbUserMapper;
import site.pushy.service.UserService;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private TbUserMapper userMapper;

    @Override
    public TbUser getUserById(String id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
