package site.pushy.jpa;

import site.pushy.jpa.data.User;

/**
 * @author Pushy
 * @since 2018/11/20 10:39
 */
public interface PostSummary {

    User getUser();

    String getId();

    String getTitle();

    String getContent();

}
