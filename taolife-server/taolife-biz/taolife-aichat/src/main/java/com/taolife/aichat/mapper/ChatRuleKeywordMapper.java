package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.ChatRuleKeyword;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 问题关键词 Mapper 接口
 *
 * @author 文二
 * @date 2026-04-18
 */
@Mapper
public interface ChatRuleKeywordMapper extends BaseMapper<ChatRuleKeyword> {

    /**
     * 按规则ID查询关键词列表（按排序号升序）
     *
     * @param ruleId 规则ID
     * @return 关键词列表
     */
    default List<ChatRuleKeyword> selectByRuleId(String ruleId) {
        return selectListByQuery(QueryWrapper.create()
                .where("rule_id = ?", ruleId)
                .orderBy("sort_order ASC"));
    }
}
