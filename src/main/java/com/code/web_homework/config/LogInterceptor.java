package com.code.web_homework.config;

import com.code.web_homework.entity.SysLog;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志拦截器。
 * <p>
 * 记录后台中「写操作」（POST 请求）的调用情况，写入 sys_log 表，
 * 便于事后追溯是谁在什么时间做了哪些改动。
 */
public class LogInterceptor implements HandlerInterceptor {

    private final LogService logService;

    /** 记录请求开始时间，用于计算耗时 */
    private static final String START_TIME = "log_start_time";

    public LogInterceptor(LogService logService) {
        this.logService = logService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 只记录 POST 请求，GET 查询量大且无副作用，记录意义不大
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return;
        }
        User user = SessionUtil.getLoginUser(request);
        if (user == null) {
            return;
        }

        Object start = request.getAttribute(START_TIME);
        long cost = start instanceof Long ? System.currentTimeMillis() - (Long) start : 0;

        SysLog sysLog = new SysLog();
        sysLog.setUserId(user.getId());
        sysLog.setUsername(user.getUsername());
        sysLog.setOperation(resolveOperation(request.getRequestURI()));
        sysLog.setMethod(request.getMethod() + " " + request.getRequestURI());
        sysLog.setParams(buildParams(request));
        sysLog.setIp(getClientIp(request));
        sysLog.setCostTime(cost);
        logService.save(sysLog);
    }

    /** 根据请求路径推断操作描述 */
    private String resolveOperation(String uri) {
        if (uri.contains("/login")) {
            return "用户登录";
        }
        if (uri.contains("/register")) {
            return "用户注册";
        }
        if (uri.contains("/audit")) {
            return "审核房源";
        }
        if (uri.contains("/house/")) {
            return uri.contains("/delete") ? "删除房源" : "保存房源";
        }
        if (uri.contains("/order/")) {
            if (uri.contains("/pay")) {
                return "支付订单";
            }
            if (uri.contains("/cancel")) {
                return "取消订单";
            }
            if (uri.contains("/checkin")) {
                return "办理入住";
            }
            if (uri.contains("/finish")) {
                return "完成订单";
            }
            return "提交订单";
        }
        if (uri.contains("/review/")) {
            return uri.contains("/reply") ? "回复评价" : "发表评价";
        }
        if (uri.contains("/notice/")) {
            return "维护公告";
        }
        if (uri.contains("/category/")) {
            return "维护分类";
        }
        if (uri.contains("/user/")) {
            return "维护用户";
        }
        return "后台操作";
    }

    /**
     * 拼接请求参数用于日志记录。
     * <p>
     * 出于安全考虑，密码类字段一律不记录，同时限制总长度避免日志表膨胀。
     */
    private String buildParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.toLowerCase().contains("password") || name.toLowerCase().contains("pwd")) {
                continue;
            }
            String value = request.getParameter(name);
            if (value != null && value.length() > 60) {
                value = value.substring(0, 60) + "…";
            }
            params.put(name, value);
        }
        if (params.isEmpty()) {
            return null;
        }
        String text = params.toString();
        return text.length() > 480 ? text.substring(0, 480) : text;
    }

    /** 获取客户端真实 IP，兼容反向代理场景 */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            return index > 0 ? ip.substring(0, index).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
