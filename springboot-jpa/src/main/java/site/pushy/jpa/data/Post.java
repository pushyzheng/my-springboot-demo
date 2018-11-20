package site.pushy.jpa.data;

import javax.persistence.*;

/**
 * @author Pushy
 * @since 2018/11/19 20:56
 */
@Entity
public class Post {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, unique = true)
    private String content;

    @ManyToOne(optional = false)
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
