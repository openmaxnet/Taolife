package com.taolife.plan.service.impl;

import com.taolife.plan.service.IPlanContentMarkerService;
import com.taolife.plan.util.PlanContentParserUtil;
import com.taolife.wisdom.entity.Acupoint;
import com.taolife.wisdom.entity.Food;
import com.taolife.wisdom.entity.Meridian;
import com.taolife.wisdom.mapper.AcupointMapper;
import com.taolife.wisdom.mapper.FoodMapper;
import com.taolife.wisdom.mapper.MeridianMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 方案内容标记服务实现
 * 在AI生成的方案内容中嵌入结构化标记，将食材/穴位/经络名称替换为可点击链接格式
 *
 * @author 文二
 * @date 2026-04-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanContentMarkerServiceImpl implements IPlanContentMarkerService {

    private final FoodMapper foodMapper;
    private final AcupointMapper acupointMapper;
    private final MeridianMapper meridianMapper;

    /**
     * 处理饮食方案内容，将食材名称替换为结构化标记链接
     *
     * @param content 饮食方案原始内容
     * @return 标记处理后的内容
     */
    @Override
    public String processFoodContent(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        List<String> names = PlanContentParserUtil.extractFoodItems(content);
        if (names.isEmpty()) {
            return content;
        }
        Map<String, String> nameToId = new HashMap<>();
        for (String name : names) {
            Food food = foodMapper.selectByName(name);
            if (food != null) {
                nameToId.put(name, food.getId());
            }
        }
        if (nameToId.isEmpty()) {
            return content;
        }
        return replaceNamesWithMarkers(content, nameToId, "food");
    }

    /**
     * 处理穴位方案内容，将穴位名称替换为结构化标记链接
     *
     * @param content 穴位方案原始内容
     * @return 标记处理后的内容
     */
    @Override
    public String processAcupointContent(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        List<String> names = PlanContentParserUtil.extractAcupointItems(content);
        if (names.isEmpty()) {
            return content;
        }
        Map<String, String> nameToId = new HashMap<>();
        for (String name : names) {
            Acupoint acupoint = acupointMapper.selectByName(name);
            if (acupoint != null) {
                nameToId.put(name, acupoint.getId());
            }
        }
        if (nameToId.isEmpty()) {
            return content;
        }
        return replaceNamesWithMarkers(content, nameToId, "acupoint");
    }

    /**
     * 处理经络方案内容，将经络名称替换为结构化标记链接
     *
     * @param content 经络方案原始内容
     * @return 标记处理后的内容
     */
    @Override
    public String processMeridianContent(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        List<String> names = PlanContentParserUtil.extractMeridianItems(content);
        if (names.isEmpty()) {
            return content;
        }
        Map<String, String> nameToId = new HashMap<>();
        for (String name : names) {
            Meridian meridian = meridianMapper.selectByName(name);
            if (meridian != null) {
                nameToId.put(name, meridian.getId());
            }
        }
        if (nameToId.isEmpty()) {
            return content;
        }
        return replaceNamesWithMarkers(content, nameToId, "meridian");
    }

    /**
     * 将内容中的名称替换为 [name](type:id) 格式
     * 只替换已经确认数据库中存在ID的名称
     */
    private String replaceNamesWithMarkers(String content, Map<String, String> nameToId, String type) {
        String result = content;
        for (Map.Entry<String, String> entry : nameToId.entrySet()) {
            String name = entry.getKey();
            String id = entry.getValue();
            // 使用精确匹配（处理特殊字符避免正则注入）
            String pattern = Pattern.quote(name);
            String replacement = "[" + name + "](" + type + ":" + id + ")";
            result = result.replaceAll(pattern, replacement);
        }
        return result;
    }
}
