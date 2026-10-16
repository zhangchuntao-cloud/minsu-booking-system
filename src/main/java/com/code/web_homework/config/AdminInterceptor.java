package com.code.web_homework.config;

import com.code.web_homework.common.Constants;
import com.code.web_homework.common.Result;
import com.code.web_homework.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台权限拦截器。
 * <p>
 * 后台管理功能只允许管理员与房东访问，普通会员即使已登录也会被拦截。
 * 该拦截器在 {@link LoginInterceptor} 之后执行，因此可以直接假定用户已登录。
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        User user = SessionUtil.getLoginUser(request);
        if (user == null) {
            // 理论上不会走到这里，兜底处理保证安全
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        if (Constants.ROLE_ADMIN.equals(user.getRoleCode())
                || Constants.ROLE_HOST.equals(user.getRoleCode())) {
            return true;
        }

        if (SessionUtil.isAjax(request)) {
            SessionUtil.writeJson(response, Result.CODE_FORBIDDEN, "当前账号没有后台管理权限");
        } else {
            response.sendRedirect(request.getContextPath() + "/error/403");
        }
        return false;
    }
}
