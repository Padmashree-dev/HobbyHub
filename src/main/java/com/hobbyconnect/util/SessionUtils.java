package com.hobbyconnect.util;

import com.hobbyconnect.model.User;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USER_NAME = "userName";
    public static final String SESSION_USER_USERNAME = "userUsername";
    public static final String SESSION_USER_AVATAR = "userAvatar";

    public static Long getLoggedInUserId(HttpSession session) {
        if (session == null) return null;
        Object id = session.getAttribute(SESSION_USER_ID);
        return id instanceof Long ? (Long) id : null;
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getLoggedInUserId(session) != null;
    }

    public static void setLoggedInUser(HttpSession session, User user) {
        if (session == null || user == null) return;
        session.setAttribute(SESSION_USER_ID, user.getId());
        session.setAttribute(SESSION_USER_NAME, user.getFullName());
        session.setAttribute(SESSION_USER_USERNAME, user.getUsername());
        session.setAttribute(SESSION_USER_AVATAR, user.getAvatar());
    }

    public static void updateSessionUser(HttpSession session, User user) {
        setLoggedInUser(session, user);
    }

    public static void clearSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
