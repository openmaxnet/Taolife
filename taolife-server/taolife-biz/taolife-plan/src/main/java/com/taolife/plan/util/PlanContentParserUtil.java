package com.taolife.plan.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方案内容解析工具类
 * 从AI生成的Markdown内容中提取具体项目（食材、运动、穴位、经络、生活建议）
 *
 * @author 文二
 * @date 2026-04-08
 */
public class PlanContentParserUtil {

    /**
     * 编号列表项匹配模式：数字+标点+内容+可选分隔符+描述
     * 匹配如 "1. 山药 - 健脾益气" 或 "1、山药：健脾益气" 或 "1.山药 健脾益气"
     */
    private static final Pattern ITEM_PATTERN = Pattern.compile(
            "(?:^|\\n)\\s*(?:\\d+|[一二三四五六七八九十])[.、）)）]\\s*(.+?)(?:\\s*[-–—:：]\\s*.+?)?(?:\\n|$)"
    );

    private PlanContentParserUtil() {
    }

    /**
     * 从饮食方案Markdown中提取推荐食材名称
     * 查找"推荐食材"章节下的编号列表
     *
     * @param markdown AI生成的饮食方案Markdown内容
     * @return 提取到的食材名称列表
     */
    public static List<String> extractFoodItems(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> items = extractItemsFromSection(markdown,
                "推荐食材|推荐食物|适宜食材|适合食材|建议食材|宜食|推荐.*食");
        if (items.isEmpty()) {
            // 兜底：直接从全文提取
            items = extractAllItems(markdown);
        }
        // 过滤掉明显不是食材的项（如"三餐规律"）
        return filterByLength(items, 2, 6);
    }

    /**
     * 从运动方案Markdown中提取运动项目名称
     *
     * @param markdown AI生成的运动方案Markdown内容
     * @return 提取到的运动项目名称列表
     */
    public static List<String> extractExerciseItems(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> items = extractItemsFromSection(markdown,
                "推荐运动|运动项目|运动方式|适合运动|建议运动|推荐.*运动");
        if (items.isEmpty()) {
            items = extractAllItems(markdown);
        }
        return filterByLength(items, 2, 8);
    }

    /**
     * 从穴位方案Markdown中提取穴位名称
     *
     * @param markdown AI生成的穴位方案Markdown内容
     * @return 提取到的穴位名称列表
     */
    public static List<String> extractAcupointItems(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> items = extractItemsFromSection(markdown,
                "推荐穴位|穴位推荐|按摩穴位|重点穴位|建议穴位|常用穴位");
        if (items.isEmpty()) {
            items = extractAllItems(markdown);
        }
        return filterByLength(items, 2, 5);
    }

    /**
     * 从经络方案Markdown中提取经络名称
     *
     * @param markdown AI生成的经络方案Markdown内容
     * @return 提取到的经络名称列表
     */
    public static List<String> extractMeridianItems(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> items = extractItemsFromSection(markdown,
                "推荐经络|经络推荐|重点经络|调理经络|建议经络|常用经络");
        if (items.isEmpty()) {
            items = extractAllItems(markdown);
        }
        return filterByLength(items, 3, 10);
    }

    /**
     * 从生活方式方案Markdown中提取生活建议项
     *
     * @param markdown AI生成的生活方案Markdown内容
     * @return 提取到的生活建议列表
     */
    public static List<String> extractLifestyleItems(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> allItems = new ArrayList<>();

        // 从多个可能章节中提取
        String[] sectionKeywords = {
                "睡眠建议|睡眠.*建议|入睡|起床",
                "情志调节|情绪管理|心理调节|情志.*方法",
                "日常起居|起居建议|生活.*建议|起居.*注意",
                "养生建议|其他建议|注意事项"
        };

        for (String keywords : sectionKeywords) {
            List<String> items = extractItemsFromSection(markdown, keywords);
            if (!items.isEmpty()) {
                allItems.addAll(items);
            }
        }

        if (allItems.isEmpty()) {
            allItems = extractAllItems(markdown);
        }

        return filterByLength(allItems, 2, 10);
    }

    /**
     * 从指定章节中提取编号列表项
     *
     * @param markdown    完整Markdown文本
     * @param sectionRegex 章节标题的正则（用于定位章节）
     * @return 提取到的项目名称列表
     */
    private static List<String> extractItemsFromSection(String markdown, String sectionRegex) {
        List<String> result = new ArrayList<>();

        // 按行分割，定位章节范围
        String[] lines = markdown.split("\\n");
        int sectionStart = -1;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // 检查是否是目标章节标题
            if (line.matches(".*(?:#+\\s*|\\*{1,2})?(?:一|二|三|四|五|六|七|八|九|十)?[、.]?" +
                    "(?:" + sectionRegex + ").*") ||
                    line.matches(".*[" + sectionRegex + "].*")) {
                sectionStart = i;
                continue;
            }

            // 如果已经找到章节，开始提取项目
            if (sectionStart >= 0) {
                // 遇到下一个一级/二级标题，停止提取
                if (i > sectionStart + 1 && line.matches("^#{1,2}\\s+.+")) {
                    break;
                }
                // 遇到空行也可能表示章节结束
                if (i > sectionStart + 1 && line.isEmpty() && result.size() >= 3) {
                    break;
                }

                // 用编号列表模式匹配
                Matcher matcher = ITEM_PATTERN.matcher(line);
                if (matcher.find()) {
                    String item = matcher.group(1).trim();
                    // 清理 Markdown 格式符号
                    item = item.replaceAll("[*`\\[\\]#]", "").trim();
                    if (!item.isEmpty() && item.length() >= 2) {
                        result.add(item);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 从全文提取所有编号列表项（兜底方案）
     *
     * @param markdown 完整Markdown文本
     * @return 提取到的所有项目名称列表
     */
    private static List<String> extractAllItems(String markdown) {
        List<String> result = new ArrayList<>();
        Matcher matcher = ITEM_PATTERN.matcher(markdown);
        while (matcher.find()) {
            String item = matcher.group(1).trim();
            item = item.replaceAll("[*`\\[\\]#]", "").trim();
            if (!item.isEmpty() && item.length() >= 2) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 按名称长度过滤项目，去除过短或过长的项，并去重
     *
     * @param items  原始项目列表
     * @param minLen 最小长度
     * @param maxLen 最大长度
     * @return 过滤后的项目列表
     */
    private static List<String> filterByLength(List<String> items, int minLen, int maxLen) {
        List<String> filtered = new ArrayList<>();
        for (String item : items) {
            if (item.length() >= minLen && item.length() <= maxLen) {
                // 去重
                if (!filtered.contains(item)) {
                    filtered.add(item);
                }
            }
        }
        return filtered;
    }
}
