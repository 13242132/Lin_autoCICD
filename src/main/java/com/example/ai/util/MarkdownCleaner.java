package com.example.ai.util;

import java.util.regex.Pattern;

/**
 * Markdown清理工具类 - 简洁高效版本
 * 用于清除AI生成文本中的Markdown格式标记
 */
public final class MarkdownCleaner {

    // 私有构造器，防止实例化
    private MarkdownCleaner() {}

    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[a-zA-Z]*\\s*([^`]*)\\s*```", Pattern.DOTALL);
    private static final Pattern INLINE_CODE_PATTERN = Pattern.compile("`([^`]*)`");
    private static final Pattern HEADER_PATTERN = Pattern.compile("^\\s*#+\\s*(.*?)(?:\\r?\\n|$)", Pattern.MULTILINE);
    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*([^*]+)\\*\\*");
    private static final Pattern ITALIC_PATTERN = Pattern.compile("(^|\\s)\\*([^*]+)\\*(?=\\s|$)");

    /**
     * 清除所有常见的Markdown标记，包括：
     * - 代码块（```java ... ```）
     * - 行内代码（`code`）
     * - 标题（# 标题）
     * - 粗体（**text**）
     * - 斜体（*text*）
     *
     * @param content 输入文本（可为 null）
     * @return 清理后的文本，null 返回 null
     */
    public static String clean(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        return CODE_BLOCK_PATTERN.matcher(content)
                .replaceAll("$1")                     // 1. 提取代码块内容
                .replaceAll(INLINE_CODE_PATTERN.pattern(), "$1")  // 2. 行内代码
                .replaceAll(HEADER_PATTERN.pattern(), "$1\n")     // 3. 标题转纯文本
                .replaceAll(BOLD_PATTERN.pattern(), "$1")         // 4. 粗体
                .replaceAll(ITALIC_PATTERN.pattern(), "$1$2$1");  // 5. 斜体（保留空格）
    }

    // ================= 可选：独立方法（按需调用）=================

    public static String removeCodeBlocks(String content) {
        return content == null ? null : CODE_BLOCK_PATTERN.matcher(content).replaceAll("$1");
    }

    public static String removeInlineCode(String content) {
        return content == null ? null : INLINE_CODE_PATTERN.matcher(content).replaceAll("$1");
    }

    public static String removeHeaders(String content) {
        return content == null ? null : HEADER_PATTERN.matcher(content).replaceAll("$1\n");
    }

    public static String removeBold(String content) {
        return content == null ? null : BOLD_PATTERN.matcher(content).replaceAll("$1");
    }

    public static String removeItalic(String content) {
        return content == null ? null : ITALIC_PATTERN.matcher(content).replaceAll("$1$2$1");
    }
}