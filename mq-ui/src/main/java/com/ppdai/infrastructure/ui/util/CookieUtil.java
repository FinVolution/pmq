package com.ppdai.infrastructure.ui.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getUserName(HttpServletRequest request){
        //获取一个cookie数组
        Cookie[] cookies = request.getCookies();
        String userName="";
        if (cookies!=null) {
            Cookie cookie = getCookie(request, "userSessionId");
            if (cookie == null) {
                return "";
            }
            try {
                return DesUtil.decrypt(cookie.getValue());
            } catch (Exception e) {
                return "";
            }

        }
        return userName;
    }

    public static Cookie getCookie(HttpServletRequest request, String key) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie temp : cks) {
                if (temp.getName().equals(key))
                    return temp;
            }
        }
        return null;
    }
}
