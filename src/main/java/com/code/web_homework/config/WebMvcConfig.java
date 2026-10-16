package com.code.web_homework.config;

import com.code.web_homework.service.FileService;
import com.code.web_homework.service.LogService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Spring MVC 全局配置。
 * <p>
 * 主要完成两件事：注册拦截器链、把外部上传目录映射为静态资源路径。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LogService logService;
    private final FileService fileService;

    public WebMvcConfig(LogService logService, FileService fileService) {
        this.logService = logService;
        this.fileService = fileService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 登录校验：个人中心、下单、评价、收藏以及全部后台路径都需要登录
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/user/**", "/order/**", "/review/**", "/favorite/**", "/admin/**")
                .order(1);

        // 2. 权限校验：后台路径还需要管理员或房东身份，在登录校验之后执行
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**")
                .order(2);

        // 3. 操作日志：记录后台的写操作
        registry.addInterceptor(new LogInterceptor(logService))
                .addPathPatterns("/admin/**", "/order/**", "/review/**", "/user/**")
                .order(3);
    }

    /**
     * 把本地上传目录映射为可访问的静态资源。
     * <p>
     * 使用 {@code file:} 前缀指向文件系统路径，页面中即可通过
     * {@code /upload/20260101/xxx.jpg} 的形式访问到上传的图片。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(fileService.getUploadPath()).toUri().toString();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
