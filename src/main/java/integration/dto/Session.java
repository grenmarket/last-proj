package integration.dto;

public class Session {

    public InnerSession getSession() {
        return session;
    }

    public void setSession(InnerSession session) {
        this.session = session;
    }

    public static class InnerSession {
        private String name;
        private String key;
        private int subscriber;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(int subscriber) {
            this.subscriber = subscriber;
        }
    }

    private InnerSession session;

}
