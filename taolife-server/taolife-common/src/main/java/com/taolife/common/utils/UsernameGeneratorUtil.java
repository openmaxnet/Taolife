package com.taolife.common.utils;

import java.security.SecureRandom;

/**
 * 用户名生成工具类
 * 生成随机字母数字组合的用户名
 *
 * @author 文二
 * @date 2026-04-18
 */
public class UsernameGeneratorUtil {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int USERNAME_LENGTH = 10;

    /**
     * 生成随机字母数字组合的用户名（10位）
     *
     * @return 随机用户名
     */
    public static String generate() {
        StringBuilder username = new StringBuilder(USERNAME_LENGTH);
        for (int i = 0; i < USERNAME_LENGTH; i++) {
            username.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return username.toString();
    }
}