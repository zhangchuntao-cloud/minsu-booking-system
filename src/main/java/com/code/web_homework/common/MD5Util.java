package com.code.web_homework.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 摘要工具类。
 * <p>
 * 数据库中不保存用户明文密码，注册时先经 MD5 摘要再入库，
 * 登录时对输入密码做同样运算后比对摘要值。
 */
public final class MD5Util {

    private MD5Util() {
    }

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    /**
     * 计算字符串的 MD5 摘要值。
     *
     * @param text 原文
     * @return 32 位小写十六进制摘要
     */
    public static String encrypt(String text) {
        if (text == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(HEX_DIGITS[(b >> 4) & 0x0F]);
                sb.append(HEX_DIGITS[b & 0x0F]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // JDK 标准实现必然支持 MD5，此处不会发生
            throw new IllegalStateException("当前环境不支持 MD5 算法", e);
        }
    }

    /**
     * 校验明文密码与密文是否匹配。
     */
    public static boolean matches(String rawText, String encrypted) {
        if (rawText == null || encrypted == null) {
            return false;
        }
        return encrypt(rawText).equalsIgnoreCase(encrypted);
    }
}
