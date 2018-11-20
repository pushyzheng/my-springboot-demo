package site.pushy.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    //@Cacheable(value = "poemInfo", key = "'user_'+#id")
    @Cacheable(value = "poemInfo")
    public TbUser getUserById(String id) {
        System.out.println("进入实现类获取数据 ...");
        return new TbUser("Pushy");
    }
}
