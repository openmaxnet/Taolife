package com.taolife.wisdom.service;

import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.ArticleQueryParam;
import com.taolife.wisdom.param.ArticleSaveParam;
import com.taolife.wisdom.vo.ArticleDetailVO;
import com.taolife.wisdom.vo.ArticleListVO;

/**
 * 文章服务接口
 * 定义文章相关的业务操作
 *
 * @author 文二
 * @date 2026-03-21
 */
public interface IArticleService {

    /**
     * 获取文章列表
     * 根据条件分页获取文章信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<ArticleListVO> getArticlePage(ArticleQueryParam param);

    /**
     * 获取文章详情
     * 根据文章ID获取文章的详细信息
     *
     * @param id 文章ID
     * @return 文章详细信息
     */
    ArticleDetailVO getArticleDetail(String id);

    /**
     * 创建文章
     *
     * @param param 文章创建参数
     */
    void createArticle(ArticleSaveParam param);

    /**
     * 修改文章信息
     *
     * @param id    文章ID
     * @param param 文章修改参数
     */
    void modifyArticleInfo(String id, ArticleSaveParam param);

    /**
     * 删除文章（逻辑删除）
     *
     * @param id 文章ID
     */
    void removeArticle(String id);

    /**
     * 修改文章状态
     *
     * @param id         文章ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifyArticleStatus(String id, Integer isDisabled);
}
