package com.seeyoungryu.connecti.fixture;

public class TestInfoFixture {

    public static TestInfo get() {
        TestInfo info = new TestInfo();
        info.setPostId(1L);
        info.setTitle("title");
        info.setBody("body");
        info.setUserName("userName"); // userName 추가
        info.setPassword("password"); // password 추가
        return info;
    }

    public static class TestInfo {
        private Long postId;
        private String userName;
        private String password;
        private String title;
        private String body;

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}
