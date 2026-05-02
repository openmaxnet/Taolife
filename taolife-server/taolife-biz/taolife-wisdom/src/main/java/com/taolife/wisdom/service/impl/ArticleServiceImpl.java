package com.taolife.wisdom.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.Article;
import com.taolife.wisdom.enums.ArticleCategoryEnum;
import com.taolife.wisdom.mapper.ArticleMapper;
import com.taolife.wisdom.param.ArticleQueryParam;
import com.taolife.wisdom.param.ArticleSaveParam;
import com.taolife.wisdom.service.IArticleService;
import com.taolife.wisdom.vo.ArticleDetailVO;
import com.taolife.wisdom.vo.ArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 * 实现文章管理的具体业务逻辑
 *
 * @author 文二
 * @date 2026-03-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements IArticleService {

    private final ArticleMapper articleMapper;

    /**
     * 获取文章列表
     * 根据条件分页获取文章数据
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<ArticleListVO> getArticlePage(ArticleQueryParam param) {
        // 执行分页查询
        Page<Article> page = articleMapper.selectArticlePage(param);

        // 转换为VO列表
        List<ArticleListVO> voList;
        if (page.getRecords() != null) {
            List<Article> records = page.getRecords();
            // 如果传入了体质编码，体质匹配的文章排到前面
            if (param.getConstitutionCode() != null && !param.getConstitutionCode().isEmpty()) {
                records = sortByConstitutionMatch(records, param.getConstitutionCode());
            }
            voList = records.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        } else {
            voList = Collections.emptyList();
        }

        return new PageResult<>(voList, param.getPageNo(), param.getPageSize(), page.getTotalRow());
    }

    /**
     * 获取文章详情
     * 根据文章ID查询文章详情，如果文章不存在则抛出业务异常
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Override
    public ArticleDetailVO getArticleDetail(String id) {
        // 根据ID查询文章
        Article article = articleMapper.selectById(id);

        if (article == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "文章不存在");
        }

        // 转换为详情VO
        return convertToDetailVO(article);
    }

    /**
     * 创建文章
     *
     * @param param 文章创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createArticle(ArticleSaveParam param) {
        if (param.getTitle() == null || param.getTitle().isEmpty()) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "标题不能为空");
        }

        Article article = new Article();
        article.setTitle(param.getTitle());
        article.setSubtitle(param.getSubtitle());
        article.setCategory(param.getCategory());
        article.setTags(param.getTags());
        article.setCoverImageUrl(param.getCoverImageUrl());
        article.setSummary(param.getSummary());
        article.setContent(param.getContent());
        article.setContentType(param.getContentType());
        article.setAuthor(param.getAuthor());
        article.setSource(param.getSource());
        article.setRelatedConstitutionCodes(param.getRelatedConstitutionCodes());
        article.setRelatedSeason(param.getRelatedSeason());
        article.setRelatedSolarTerm(param.getRelatedSolarTerm());
        article.setIsRecommended(param.getIsRecommended() != null ? param.getIsRecommended() : 0);
        article.setIsFeatured(param.getIsFeatured() != null ? param.getIsFeatured() : 0);
        article.setReadCount(0);
        article.setLikeCount(0);
        article.setCollectCount(0);
        article.setShareCount(0);
        article.setIsDeleted(0);
        article.setPublishTime(param.getPublishTime());
        article.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        article.setStatus(param.getStatus() != null ? param.getStatus() : 0);
        article.setCreateTime(LocalDateTime.now());
        articleMapper.insert(article);
        log.info("创建文章成功：title={}", param.getTitle());
    }

    /**
     * 修改文章信息
     *
     * @param id    文章ID
     * @param param 文章修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyArticleInfo(String id, ArticleSaveParam param) {
        Article article = articleMapper.selectByIdForAdmin(id);
        if (article == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文章不存在");
        }

        if (param.getTitle() != null && !param.getTitle().isEmpty()) {
            article.setTitle(param.getTitle());
        }
        if (param.getSubtitle() != null) {
            article.setSubtitle(param.getSubtitle());
        }
        if (param.getCategory() != null) {
            article.setCategory(param.getCategory());
        }
        if (param.getTags() != null) {
            article.setTags(param.getTags());
        }
        if (param.getCoverImageUrl() != null) {
            article.setCoverImageUrl(param.getCoverImageUrl());
        }
        if (param.getSummary() != null) {
            article.setSummary(param.getSummary());
        }
        if (param.getContent() != null) {
            article.setContent(param.getContent());
        }
        if (param.getContentType() != null) {
            article.setContentType(param.getContentType());
        }
        if (param.getAuthor() != null) {
            article.setAuthor(param.getAuthor());
        }
        if (param.getSource() != null) {
            article.setSource(param.getSource());
        }
        if (param.getRelatedConstitutionCodes() != null) {
            article.setRelatedConstitutionCodes(param.getRelatedConstitutionCodes());
        }
        if (param.getRelatedSeason() != null) {
            article.setRelatedSeason(param.getRelatedSeason());
        }
        if (param.getRelatedSolarTerm() != null) {
            article.setRelatedSolarTerm(param.getRelatedSolarTerm());
        }
        if (param.getIsRecommended() != null) {
            article.setIsRecommended(param.getIsRecommended());
        }
        if (param.getIsFeatured() != null) {
            article.setIsFeatured(param.getIsFeatured());
        }
        if (param.getPublishTime() != null) {
            article.setPublishTime(param.getPublishTime());
        }
        if (param.getSortOrder() != null) {
            article.setSortOrder(param.getSortOrder());
        }
        if (param.getStatus() != null) {
            article.setStatus(param.getStatus());
        }
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
        log.info("修改文章信息成功：id={}", id);
    }

    /**
     * 删除文章（逻辑删除）
     *
     * @param id 文章ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeArticle(String id) {
        Article article = articleMapper.selectByIdForAdmin(id);
        if (article == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文章不存在");
        }

        article.setIsDeleted(1);
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
        log.info("删除文章成功：id={}", id);
    }

    /**
     * 修改文章状态
     *
     * @param id         文章ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyArticleStatus(String id, Integer isDisabled) {
        Article article = articleMapper.selectByIdForAdmin(id);
        if (article == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文章不存在");
        }

        // isDisabled=0 表示启用（状态设为已发布1），isDisabled=1 表示禁用（状态设为草稿0）
        article.setStatus(isDisabled == 0 ? 1 : 0);
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
        log.info("修改文章状态成功：id={}, isDisabled={}", id, isDisabled);
    }

    /**
     * 转换为列表VO
     *
     * @param article 文章实体
     * @return 列表VO
     */
    private ArticleListVO convertToListVO(Article article) {
        ArticleListVO vo = new ArticleListVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSubtitle(article.getSubtitle());
        vo.setCategory(article.getCategory());
        vo.setCategoryName(getCategoryName(article.getCategory()));
        vo.setTags(article.getTags());
        vo.setCoverImageUrl(article.getCoverImageUrl());
        // 简要显示摘要，取前100个字符
        vo.setSummary(truncateText(article.getSummary(), 100));
        vo.setAuthor(article.getAuthor());
        vo.setSource(article.getSource());
        vo.setReadCount(article.getReadCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setCollectCount(article.getCollectCount());
        vo.setIsRecommended(article.getIsRecommended());
        vo.setIsFeatured(article.getIsFeatured());
        vo.setPublishTime(article.getPublishTime());
        return vo;
    }

    /**
     * 转换为详情VO
     *
     * @param article 文章实体
     * @return 详情VO
     */
    private ArticleDetailVO convertToDetailVO(Article article) {
        ArticleDetailVO vo = new ArticleDetailVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSubtitle(article.getSubtitle());
        vo.setCategory(article.getCategory());
        vo.setCategoryName(getCategoryName(article.getCategory()));
        vo.setTags(article.getTags());
        vo.setCoverImageUrl(article.getCoverImageUrl());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setContentType(article.getContentType());
        vo.setAuthor(article.getAuthor());
        vo.setSource(article.getSource());
        vo.setRelatedConstitutionCodes(article.getRelatedConstitutionCodes());
        vo.setRelatedSeason(article.getRelatedSeason());
        vo.setRelatedSolarTerm(article.getRelatedSolarTerm());
        vo.setReadCount(article.getReadCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setCollectCount(article.getCollectCount());
        vo.setShareCount(article.getShareCount());
        vo.setIsRecommended(article.getIsRecommended());
        vo.setIsFeatured(article.getIsFeatured());
        vo.setPublishTime(article.getPublishTime());
        return vo;
    }

    /**
     * 获取分类名称
     *
     * @param category 分类值
     * @return 分类名称
     */
    private String getCategoryName(Integer category) {
        ArticleCategoryEnum categoryEnum = ArticleCategoryEnum.getByValue(category);
        return categoryEnum != null ? categoryEnum.getName() : null;
    }

    /**
     * 截断文本
     *
     * @param text 原始文本
     * @param maxLength 最大长度
     * @return 截断后的文本
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    /**
     * 按体质匹配度排序：匹配的文章排到前面
     *
     * @param articles          文章列表
     * @param constitutionCode  体质编码
     * @return 排序后的文章列表
     */
    private List<Article> sortByConstitutionMatch(List<Article> articles, String constitutionCode) {
        if (articles == null || articles.isEmpty() || constitutionCode == null || constitutionCode.isEmpty()) {
            return articles;
        }
        return articles.stream()
            .sorted((a, b) -> {
                boolean aMatch = isConstitutionMatch(a, constitutionCode);
                boolean bMatch = isConstitutionMatch(b, constitutionCode);
                if (aMatch && !bMatch) return -1;
                if (!aMatch && bMatch) return 1;
                return 0;
            })
            .collect(Collectors.toList());
    }

    /**
     * 判断文章是否关联指定体质
     *
     * @param article           文章
     * @param constitutionCode  体质编码
     * @return 是否匹配
     */
    private boolean isConstitutionMatch(Article article, String constitutionCode) {
        if (article == null || constitutionCode == null) {
            return false;
        }
        String relatedCodes = article.getRelatedConstitutionCodes();
        if (relatedCodes == null || relatedCodes.isEmpty()) {
            return false;
        }
        // relatedConstitutionCodes 可能是 JSON 数组字符串或逗号分隔字符串
        return relatedCodes.contains(constitutionCode);
    }
}
