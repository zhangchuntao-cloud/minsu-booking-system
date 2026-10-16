package com.code.web_homework.config;

import com.code.web_homework.common.Constants;
import com.code.web_homework.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;

/**
 * 登录状态与响应输出工具类。
 * <p>
 * 集中处理 Session 中登录用户的读写、请求类型判断以及拦截器中的
 * JSON 响应输出，供各个拦截器与 Controller 复用。
 */
public final class SessionUtil {

    private SessionUtil() {
    }

    /** 从 Session 中取出当前登录用户，未登录返回 null */
    public static User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(Constants.SESSION_USER);
    }

    /** 把登录用户写入 Session */
    public static void setLoginUser(HttpServletRequest request, User user) {
        request.getSession(true).setAttribute(Constants.SESSION_USER, user);
    }

    /** 清除登录状态 */
    public static void removeLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Constants.SESSION_USER);
        }
    }

    /**
     * 判断是否为 AJAX 异步请求。
     * <p>
     * 异步请求需要返回 JSON，而普通页面请求应当重定向到登录页，
     * 因此拦截器中必须区分两者，否则前端会拿到一整页 HTML 而解析失败。
     */
    public static boolean isAjax(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return true;
        }
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/api/");
    }

    /**
     * 直接向响应流写出统一格式的 JSON。
     * <p>
     * 拦截器执行时 Spring MVC 的消息转换器尚未介入，因此这里手工拼装
     * 固定结构的 JSON 文本，既避免额外依赖，也不会有序列化开销。
     */
    public static void writeJson(HttpServletResponse response, int code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write("{\"code\":" + code + ",\"msg\":\"" + escape(msg) + "\",\"data\":null}");
            writer.flush();
        }
    }

    /** 转义 JSON 字符串中的特殊字符 */
    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(text.length() + 8);
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
