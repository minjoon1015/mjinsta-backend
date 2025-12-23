package back_end.springboot.component;

public class RedisKeyManager {
    public static final String KEY_ACTIVE_USERS = "ACTIVE_USERS";
    
    public static String getUserInterestsKey(String userId) {
        return "USER_INTERESTS:" + userId;
    }

    public static String getUserReadHistoryKey(String userId) {
        return "USER_POST_VIEW_HISTORY:" + userId;
    }

    public static String getUserFeedKey(String userId) {
        return "USER:FEED:" + userId;
    }

    public static String getUserFeedPointerKey(String userId) {
        return "USER:FEED_POINTER:" + userId;
    }

    public static String getFeedSizeKey(String userId) {
        return "USER:FEED:size:" + userId;
    }
}
