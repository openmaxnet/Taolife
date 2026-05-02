package com.taolife.aichat.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.aichat.entity.SensitiveWord;
import com.taolife.aichat.param.SensitiveWordPageAdminParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 敏感词Mapper接口
 *
 * @author 文二
 * @date 2026-03-26
 */
@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {

    /**
     * 查询所有启用的敏感词
     *
     * @return 敏感词列表
     */
    default List<SensitiveWord> selectAllEnabled() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(SensitiveWord::getIsEnabled).eq(1)
                .and(SensitiveWord::getIsDeleted).eq(0)
        );
    }

    /**
     * 根据类型查询敏感词
     *
     * @param wordType 敏感词类型
     * @return 敏感词列表
     */
    default List<SensitiveWord> selectByWordType(Integer wordType) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(SensitiveWord::getWordType).eq(wordType)
                .and(SensitiveWord::getIsEnabled).eq(1)
                .and(SensitiveWord::getIsDeleted).eq(0)
        );
    }

    /**
     * 根据严重程度查询敏感词
     *
     * @param severity 严重程度
     * @return 敏感词列表
     */
    default List<SensitiveWord> selectBySeverity(Integer severity) {
        return selectListByQuery(
            QueryWrapper.create()
                .where(SensitiveWord::getSeverity).eq(severity)
                .and(SensitiveWord::getIsEnabled).eq(1)
                .and(SensitiveWord::getIsDeleted).eq(0)
        );
    }

    /**
     * 分页查询敏感词列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<SensitiveWord> selectPageByParam(SensitiveWordPageAdminParam param) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SensitiveWord::getIsDeleted).eq(0);

        if (param.getWordType() != null) {
            wrapper.and(SensitiveWord::getWordType).eq(param.getWordType());
        }
        if (param.getKeyword() != null && !param.getKeyword().trim().isEmpty()) {
            wrapper.and(SensitiveWord::getWord).like(param.getKeyword());
        }

        wrapper.orderBy(SensitiveWord::getCreateTime, false);
        return paginate(param.getPageNo(), param.getPageSize(), wrapper);
    }

    /**
     * 根据ID查询敏感词（包含已删除）
     *
     * @param id 敏感词ID
     * @return 敏感词
     */
    default SensitiveWord selectByIdIncludeDeleted(String id) {
        return selectOneById(id);
    }
}