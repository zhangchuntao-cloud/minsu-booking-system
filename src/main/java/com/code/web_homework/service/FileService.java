package com.code.web_homework.service;

import com.code.web_homework.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传业务逻辑层。
 * <p>
 * 上传的图片保存在应用外部的独立目录中，并通过 {@code WebMvcConfig}
 * 注册的静态资源映射对外提供访问，避免与 classpath 下的静态资源混在一起。
 */
@Service
public class FileService {

    /** 允许上传的图片扩展名 */
    private static final List<String> ALLOWED_EXT =
            Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    /** 单个文件大小上限 5MB */
    private static final long MAX_SIZE = 5 * 1024 * 1024L;

    /** 上传文件的物理保存目录 */
    private final String uploadPath;

    /** 上传文件的访问前缀 */
    private final String urlPrefix;

    public FileService(@Value("${minsu.upload-path}") String uploadPath,
                       @Value("${minsu.upload-url-prefix}") String urlPrefix) {
        this.uploadPath = uploadPath;
        this.urlPrefix = urlPrefix;
    }

    /**
     * 保存上传的图片。
     *
     * @return 可直接用于 img 标签 src 的访问地址
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片大小不能超过 5MB");
        }

        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("仅支持 jpg、png、gif、bmp、webp 格式的图片");
        }

        // 按日期分目录存放，避免单目录下文件过多影响检索
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        try {
            Path dir = Paths.get(uploadPath, dateDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(newFileName);
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BusinessException("图片保存失败：" + e.getMessage());
        }

        return urlPrefix + dateDir + "/" + newFileName;
    }

    /** 提取文件扩展名并转为小写 */
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new BusinessException("文件名不合法");
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    /** 获取上传根目录，供静态资源映射使用 */
    public String getUploadPath() {
        return uploadPath;
    }
}
