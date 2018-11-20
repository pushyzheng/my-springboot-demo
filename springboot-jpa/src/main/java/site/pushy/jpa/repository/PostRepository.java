package site.pushy.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.pushy.jpa.PostSummary;
import site.pushy.jpa.data.Post;

import java.util.List;

/**
 * @author Pushy
 * @since 2018/11/19 20:45
 */
@Repository
public interface PostRepository extends JpaRepository<Post, String> {

}
