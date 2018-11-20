package site.pushy.shirodemo.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Pushy
 * @since 2018/11/20 21:55
 */
@Entity
public class User {

    @Id
    private String id;

    private String name;

    private String password;

    private String roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
