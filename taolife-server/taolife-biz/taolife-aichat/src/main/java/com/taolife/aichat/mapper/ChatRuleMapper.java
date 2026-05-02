package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.ChatRule;
import com.taolife.aichat.param.QuestionRulePageAdminParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 问题分类规则Mapper接口
 *
 * @author 文二
 * @date 2026-03-26
 */
@Mapper
public interface ChatRuleMapper extends BaseMapper<ChatRule> {

    /**
     * 查询所有启用的规则
     *
     * @return 规则列表
     */
    default List<ChatRule> selectAllEnabled() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(ChatRule::getIsEnabled).eq(1)
                .and(ChatRule::getIsDeleted).eq(0)
                .orderBy(ChatRule::getPriority, false)
        );
    }

    /**
     * 根据分类编码查询规则
     *
     * @param categoryCode 分类编码
     * @return 规则
     */
    default ChatRule selectByCategoryCode(String categoryCode) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(ChatRule::getCategoryCode).eq(categoryCode)
                .and(ChatRule::getIsEnabled).eq(1)
                .and(ChatRule::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询分类规则列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<ChatRule> selectPageByParam(QuestionRulePageAdminParam param) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(ChatRule::getIsDeleted).eq(0);

        if (param.getKeyword() != null && !param.getKeyword().trim().isEmpty()) {
            wrapper.and(ChatRule::getCategoryName).like(param.getKeyword());
        }

        wrapper.orderBy(ChatRule::getPriority, false);
        return paginate(param.getPageNo(), param.getPageSize(), wrapper);
    }
}
