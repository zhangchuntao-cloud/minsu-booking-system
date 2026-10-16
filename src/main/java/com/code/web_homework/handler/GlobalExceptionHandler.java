package com.code.web_homework.handler;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 全局异常处理器。
 * <p>
 * 统一捕获各层抛出的异常并转换为友好提示：
 * 异步请求返回 JSON，页面请求渲染错误页。
 * 这样 Controller 中就不必写大量的 try-catch，代码更清爽。
 * <p>
 * 注意：异步分支必须返回 {@link ResponseEntity} 而不能直接返回业务对象。
 * 异常处理方法默认不带 {@code @ResponseBody}，若返回普通对象，
 * Spring MVC 会把它当作「视图名」去解析模板，从而抛出模板找不到的异常。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常：属于可预期的错误（如密码错误、日期冲突），
     * 只提示用户，不需要打印堆栈。
     */
    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException e, HttpServletRequest request) {
        return buildResponse(e.getMessage(), request);
    }

    /**
     * 参数校验失败：把第一条校验提示返回给用户。
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object handleValidException(Exception e, HttpServletRequest request) {
        List<FieldError> errors;
        if (e instanceof MethodArgumentNotValidException) {
            errors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
        } else {
            errors = ((BindException) e).getBindingResult().getFieldErrors();
        }
        String message = errors.isEmpty() ? "提交的参数不合法" : errors.get(0).getDefaultMessage();
        return buildResponse(message, request);
    }

    /**
     * 兜底异常：非预期错误，需要记录完整堆栈便于排查，
     * 但返回给用户的提示不包含技术细节。
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常，请求地址：{}", request.getRequestURI(), e);
        return buildResponse("系统繁忙，请稍后重试", request);
    }

    /**
     * 根据请求类型构造响应：异步请求返回 JSON，页面请求渲染错误页。
     */
    private Object buildResponse(String message, HttpServletRequest request) {
        if (SessionUtil.isAjax(request)) {
            return ResponseEntity.ok(Result.fail(message));
        }
        ModelAndView mv = new ModelAndView("error/error");
        mv.addObject("message", message);
        mv.addObject("backUrl", request.getHeader("Referer"));
        return mv;
    }
}
