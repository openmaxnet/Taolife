package com.taolife.wisdom.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.param.ExerciseQueryParam;
import com.taolife.wisdom.service.IExerciseService;
import com.taolife.wisdom.vo.ExerciseDetailVO;
import com.taolife.wisdom.vo.ExerciseListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运动知识库控制器（用户端）
 *
 * @author 文二
 * @date 2026-04-28
 */
@Slf4j
@RestController
@RequestMapping("/api/wisdom/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final IExerciseService exerciseService;

    /**
     * 获取运动列表（用户端）
     *
     * @param param 查询参数
     * @return 运动列表分页结果
     */
    @GetMapping("/getExercisePage")
    public ExceptionResult<PageResult<ExerciseListVO>> getExercisePage(ExerciseQueryParam param) {
        log.info("获取运动列表，param: {}", param);
        return ExceptionResult.success(exerciseService.getExerciseUserPage(param));
    }

    /**
     * 获取运动详情（用户端）
     *
     * @param id 运动项目ID
     * @return 运动详情VO
     */
    @GetMapping("/getExerciseDetail")
    public ExceptionResult<ExerciseDetailVO> getExerciseDetail(@RequestParam("id") String id) {
        log.info("获取运动详情，id: {}", id);
        return ExceptionResult.success(exerciseService.getExerciseUserDetail(id));
    }
}
