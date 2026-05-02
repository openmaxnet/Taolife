package com.taolife.wisdom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.ArticleQueryParam;
import com.taolife.wisdom.param.ArticleSaveParam;
import com.taolife.wisdom.service.IArticleService;
import com.taolife.wisdom.vo.ArticleDetailVO;
import com.taolife.wisdom.vo.ArticleListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章管理控制器（管理端）
 * 负责处理管理端文章相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/wisdom/admin/article")
@RequiredArgsConstructor
public class ArticleAdminController {

    private final IArticleService healthArticleService;

    /**
     * 分页获取文章列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getArticlePage")
    public ExceptionResult<PageResult<ArticleListVO>> getArticlePage(ArticleQueryParam param) {
        log.info("管理端分页获取文章列表，参数：{}", param);
        return ExceptionResult.success(healthArticleService.getArticlePage(param));
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详细信息
     */
    @GetMapping("/getArticleDetail")
    public ExceptionResult<ArticleDetailVO> getArticleDetail(@RequestParam("id") String id) {
        log.info("管理端获取文章详情，id：{}", id);
        return ExceptionResult.success(healthArticleService.getArticleDetail(id));
    }

    /**
     * 创建文章
     *
     * @param param 文章创建参数
     * @return 操作结果
     */
    @PostMapping("/createArticle")
    public ExceptionResult<Void> createArticle(@Valid @RequestBody ArticleSaveParam param) {
        log.info("管理端创建文章，参数：{}", param);
        healthArticleService.createArticle(param);
        return ExceptionResult.success();
    }

    /**
     * 修改文章信息
     *
     * @param id    文章ID
     * @param param 文章修改参数
     * @return 操作结果
     */
    @PostMapping("/modifyArticleInfo")
    public ExceptionResult<Void> modifyArticleInfo(@RequestParam("id") String id,
                                                    @Valid @RequestBody ArticleSaveParam param) {
        log.info("管理端修改文章信息，id：{}，参数：{}", id, param);
        healthArticleService.modifyArticleInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除文章（逻辑删除）
     *
     * @param id 文章ID
     * @return 操作结果
     */
    @PostMapping("/removeArticle")
    public ExceptionResult<Void> removeArticle(@RequestParam("id") String id) {
        log.info("管理端删除文章，id：{}", id);
        healthArticleService.removeArticle(id);
        return ExceptionResult.success();
    }

    /**
     * 修改文章状态
     *
     * @param id         文章ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     * @return 操作结果
     */
    @PostMapping("/modifyArticleStatus")
    public ExceptionResult<Void> modifyArticleStatus(@RequestParam("id") String id,
                                                      @RequestParam("isDisabled") Integer isDisabled) {
        log.info("管理端修改文章状态，id：{}，isDisabled：{}", id, isDisabled);
        healthArticleService.modifyArticleStatus(id, isDisabled);
        return ExceptionResult.success();
    }
}
