package site.pushy.shirodemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.pushy.shirodemo.bean.Role;

import java.util.List;

/**
 * @author Pushy
 * @since 2018/11/21 15:46
 */
public interface RoleRepository extends JpaRepository<Role, String> {

    List<Role> findByUserId(String id);

}
