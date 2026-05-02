package com.taolife.aichat.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI提示词变量正则表达式常量
 * 用于匹配 ${variable} 形式的占位符
 *
 * @author 文二
 * @date 2026-04-14
 */
public enum AiPromptVariablePattern {

    /**
     * 单例实例
     */
    INSTANCE;

    /**
     * 变量占位符正则表达式：匹配 ${variable} 形式
     * 例如：${name}, ${age}, ${content}
     */
    public final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * 获取正则匹配器
     *
     * @param input 输入字符串
     * @return Matcher对象
     */
    public Matcher matcher(CharSequence input) {
        return VARIABLE_PATTERN.matcher(input);
    }
}