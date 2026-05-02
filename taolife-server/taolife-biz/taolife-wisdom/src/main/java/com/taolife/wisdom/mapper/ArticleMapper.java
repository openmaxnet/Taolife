package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.Article;
import com.taolife.wisdom.param.ArticleQueryParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章Mapper接口
 *
 * @author 文二
 * @date 2026-03-21
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 根据ID查询文章（带逻辑删除过滤）
     *
     * @param id 文章ID
     * @return 文章对象
     */
    default Article selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Article::getId).eq(id)
                .and(Article::getIsDeleted).eq(0)
                .and(Article::getStatus).eq(1)
                .limit(1)
        );
    }

    /**
     * 根据ID查询文章（管理端，仅过滤逻辑删除，不过滤状态）
     *
     * @param id 文章ID
     * @return 文章对象
     */
    default Article selectByIdForAdmin(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(Article::getId).eq(id)
                .and(Article::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 管理端分页查询文章列表（仅过滤逻辑删除，不过滤状态）
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<Article> selectArticlePageForAdmin(ArticleQueryParam param) {
        QueryWrapper wrapper = buildAdminQueryWrapper(param);
        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 分页查询文章列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<Article> selectArticlePage(ArticleQueryParam param) {
        QueryWrapper wrapper = buildQueryWrapper(param);
        return paginate(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
    }

    /**
     * 构建文章查询条件
     *
     * @param param 查询参数
     * @return 查询条件包装器
     */
    default QueryWrapper buildQueryWrapper(ArticleQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        // 默认条件：未删除、已发布
        wrapper.where(Article::getIsDeleted).eq(0);
        wrapper.and(Article::getStatus).eq(1);

        // 分类筛选（0表示全部，不添加筛选条件）
        if (param.getCategory() != null && param.getCategory() != 0) {
            wrapper.and(Article::getCategory).eq(param.getCategory());
        }

        // 是否推荐筛选
        if (param.getIsRecommended() != null) {
            wrapper.and(Article::getIsRecommended).eq(param.getIsRecommended());
        }

        // 是否精选筛选
        if (param.getIsFeatured() != null) {
            wrapper.and(Article::getIsFeatured).eq(param.getIsFeatured());
        }

        // 关键词搜索（标题模糊匹配）
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            wrapper.and(Article::getTitle).like(param.getKeyword());
        }

        // 按发布时间倒序排列
        wrapper.orderBy(Article::getPublishTime, false);

        return wrapper;
    }

    /**
     * 构建管理端文章查询条件（仅过滤逻辑删除，不过滤状态）
     *
     * @param param 查询参数
     * @return 查询条件包装器
     */
    default QueryWrapper buildAdminQueryWrapper(ArticleQueryParam param) {
        QueryWrapper wrapper = QueryWrapper.create();

        // 默认条件：未删除（不过滤状态，管理端可查看草稿）
        wrapper.where(Article::getIsDeleted).eq(0);

        // 分类筛选（0表示全部，不添加筛选条件）
        if (param.getCategory() != null && param.getCategory() != 0) {
            wrapper.and(Article::getCategory).eq(param.getCategory());
        }

        // 是否推荐筛选
        if (param.getIsRecommended() != null) {
            wrapper.and(Article::getIsRecommended).eq(param.getIsRecommended());
        }

        // 是否精选筛选
        if (param.getIsFeatured() != null) {
            wrapper.and(Article::getIsFeatured).eq(param.getIsFeatured());
        }

        // 关键词搜索（标题模糊匹配）
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            wrapper.and(Article::getTitle).like(param.getKeyword());
        }

        // 按排序字段和发布时间倒序排列
        wrapper.orderBy(Article::getSortOrder, false);
        wrapper.orderBy(Article::getPublishTime, false);

        return wrapper;
    }
}
