package site.pushy.shirodemo.bean;

import javax.persistence.*;

/**
 * @author Pushy
 * @since 2018/11/20 21:56
 */
@Entity
public class Role {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(optional = false)
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
