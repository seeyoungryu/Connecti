package com.seeyoungryu.connecti.fixture;

import lombok.Data;


public class TestInfoFixture {

    public static TestInfo get() {
        TestInfo info = new TestInfo();
        info.setPostId(1L);
        info.setTitle("title");
        info.setBody("body");
        return info;
    }


    @Data
    public static class TestInfo {
        private Long postId;
        private String userName;
        private String password;
        private String title;
        private String body;
    }
}

