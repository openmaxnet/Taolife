package com.taolife.wisdom.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.wisdom.param.ArticleQueryParam;
import com.taolife.wisdom.service.IArticleService;
import com.taolife.wisdom.vo.ArticleDetailVO;
import com.taolife.wisdom.vo.ArticleListVO;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文章控制器
 * 处理文章相关的HTTP请求
 *
 * @author 文二
 * @date 2026-03-21
 */
@Slf4j
@RestController
@RequestMapping("/api/wisdom/article")
@RequiredArgsConstructor
public class ArticleController {

    private final IArticleService articleService;

    /**
     * 获取文章列表
     * 根据条件分页获取文章信息列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getArticlePage")
    public ExceptionResult<PageResult<ArticleListVO>> getArticlePage(ArticleQueryParam param) {
        log.info("获取文章列表，param: {}", param);
        return ExceptionResult.success(articleService.getArticlePage(param));
    }

    /**
     * 获取文章详情
     * 根据文章ID获取文章的详细信息
     *
     * @param id 文章ID
     * @return 文章详细信息
     */
    @GetMapping("/getArticleDetail")
    public ExceptionResult<ArticleDetailVO> getArticleDetail(@RequestParam("id") String id) {
        log.info("获取文章详情，id: {}", id);
        return ExceptionResult.success(articleService.getArticleDetail(id));
    }
}
