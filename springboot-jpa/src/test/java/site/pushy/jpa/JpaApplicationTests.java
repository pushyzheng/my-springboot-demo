package site.pushy.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import site.pushy.jpa.data.Post;
import site.pushy.jpa.data.User;
import site.pushy.jpa.repository.PostRepository;
import site.pushy.jpa.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PostRepository postRepository;

	@Test
	public void save() {
		User user = new User();
		user.setId(UUID.randomUUID().toString());
		user.setName("Pushy");
		user.setPassword("123");

		userRepository.save(user);
	}

	@Test
	public void findLikeName() {
		User user = userRepository.findUserByNameLike("%P%");
		System.out.println(user);
	}

	@Test
	public void getUserByName() {
		User user = userRepository.getUserByName("Pushy");
		System.out.println(user);
	}

	@Test
	public void updateUserNameById() {
		int result = userRepository
				.updateUserNameById("f9251e38-7f92-469a-8c04-7c8d2f9a7edc", "Pushy");
		if (result == 1) {
			System.out.println("更新成功");
		} else {
			System.out.println("更新失败");
		}
	}

	@Test
	public void savePosts() {
		for (int i = 0; i < 40; i++) {
			Post post = new Post();
			post.setId(UUID.randomUUID().toString());
			post.setTitle("Title " + i);
			post.setContent("Content " + i);
			postRepository.save(post);
		}
	}

	@Test
	public void paginationPost() {
		int offset = 1, count = 10;
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		// Pageable 是spring封装的分页实现类，使用的时候需要传入页数、每页条数和排序规则
		Pageable pageable = new PageRequest(offset, count, sort);
		Page<Post> page = postRepository.findAll(pageable);
		List<Post> posts = page
				.get()
				.collect(Collectors.toList());
		System.out.println(posts);
	}

	@Test
	public void leftJoinQuery() {
		List<PostSummary> posts = userRepository.listPosts("f9251e38-7f92-469a-8c04-7c8d2f9a7edc");
		for (PostSummary summary : posts) {
			System.out.println("post title： " + summary.getTitle() + " name： " + summary.getUser().getName());
		}
	}

}
