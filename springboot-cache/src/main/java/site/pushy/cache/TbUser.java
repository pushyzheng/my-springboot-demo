package site.pushy.cache;

import java.io.Serializable;

public class TbUser implements Serializable {

    private static final long serialVersionUID = -1L;

    public String name;

    public TbUser() {
    }

    public TbUser(String name) {
        this.name = name;
    }
}
