package com.ccino.ware.retrofit;

import java.util.List;

/**
 * Created by ccino on 2021/10/18.
 */
public class FriendLinkRes {

    public List<Data> data;
    public int errorCode;
    public String errorMsg;

    public static class Data {
        public String category;
        public String icon;
        public int id;
        public String link;
        public String name;
        public int order;
        public int visible;
    }
}
