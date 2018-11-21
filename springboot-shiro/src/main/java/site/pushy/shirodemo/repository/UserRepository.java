package site.pushy.shirodemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.pushy.shirodemo.bean.Role;
import site.pushy.shirodemo.bean.User;

import java.util.List;

/**
 * @author Pushy
 * @since 2018/11/21 15:33
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
