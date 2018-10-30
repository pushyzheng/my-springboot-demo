package site.pushy.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	public void contextLoads() {
		userService.getUserById("1");
		userService.getUserById("1");
		userService.getUserById("1");
	}

}
