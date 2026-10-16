package com.code.web_homework.controller;

import com.code.web_homework.common.Result;
import com.code.web_homework.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器。
 * <p>
 * 供房源编辑页通过 AJAX 异步上传封面与图集，上传成功后返回可访问的图片地址。
 */
@Controller
@RequestMapping("/admin/upload")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传单张图片（AJAX）。
     */
    @PostMapping("/image")
    @ResponseBody
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        return Result.success("上传成功", data);
    }
}
