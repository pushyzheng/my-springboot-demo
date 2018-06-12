package com.pushy.mongodbdemo.pojo;


import java.util.List;

public class Items {

    private List<ItemField> freshItemList;

    private List<ItemField> noFreshItemList;

    public List<ItemField> getFreshItemList() {
        return freshItemList;
    }

    public void setFreshItemList(List<ItemField> freshItemList) {
        this.freshItemList = freshItemList;
    }

    public List<ItemField> getNoFreshItemList() {
        return noFreshItemList;
    }

    public void setNoFreshItemList(List<ItemField> noFreshItemList) {
        this.noFreshItemList = noFreshItemList;
    }
}
