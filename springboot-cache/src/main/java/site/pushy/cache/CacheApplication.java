package site.pushy.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootApplication
@EnableCaching  // 开启缓存
@RestController
public class CacheApplication {

	@Autowired
	private UserService userService;

	@RequestMapping("/test")
	public String hello() {
		TbUser user = userService.getUserById("1");
		return user.name;
	}

	public static void main(String[] args) {
		SpringApplication.run(CacheApplication.class, args);
	}
}
