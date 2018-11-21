package site.pushy.shirodemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import site.pushy.shirodemo.bean.User;
import site.pushy.shirodemo.repository.UserRepository;
import site.pushy.shirodemo.util.JWTUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroDemoApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void contextLoads() {
		String token = JWTUtil.encode("123");
		System.out.println(token);
	}

}
