package com.code.web_homework.config;

import com.code.web_homework.common.Result;
import com.code.web_homework.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 登录校验拦截器。
 * <p>
 * 对需要登录才能访问的路径统一做前置检查，未登录时：
 * 普通页面请求重定向到登录页并携带原地址，异步请求则返回 401 的 JSON。
 * 这样业务代码中就不必再重复判断登录状态。
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        User user = SessionUtil.getLoginUser(request);
        if (user != null) {
            return true;
        }

        if (SessionUtil.isAjax(request)) {
            SessionUtil.writeJson(response, Result.CODE_UNAUTHORIZED, "登录状态已失效，请重新登录");
        } else {
            String target = request.getRequestURI();
            String query = request.getQueryString();
            if (query != null && !query.isEmpty()) {
                target = target + "?" + query;
            }
            response.sendRedirect(request.getContextPath() + "/login?redirect="
                    + URLEncoder.encode(target, StandardCharsets.UTF_8));
        }
        return false;
    }
}
