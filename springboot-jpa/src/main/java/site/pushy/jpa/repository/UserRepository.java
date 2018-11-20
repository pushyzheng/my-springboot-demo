package site.pushy.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import site.pushy.jpa.PostSummary;
import site.pushy.jpa.data.User;

import java.util.List;

/**
 * @author Pushy
 * @since 2018/11/19 20:45
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findUserById(String id);

    User findUserByName(String name);

    void deleteByName(String name);

    User findUserByNameLike(String name);

    @Query("select u from User u where u.name = ?1")
    User getUserByName(String name);

    /**
     * 更新和删除等操作必须加上@Transactional事务
     */
    @Modifying
    @Transactional
    @Query("update User u set u.name = ?2 where u.id = ?1")
    int updateUserNameById(String id, String name);

    @Modifying
    @Transactional
    @Query("delete from User where id = ?1")

    void deleteByUserId(String id);
    @Query("select p.id as id, p.title as title, u as user" +
            " from Post p left join User u on p.user = u.id where u.id = ?1")
    List<PostSummary> listPosts(String userId);



}
