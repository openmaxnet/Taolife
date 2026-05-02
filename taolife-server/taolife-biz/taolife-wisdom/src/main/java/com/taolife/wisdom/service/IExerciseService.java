package com.taolife.wisdom.service;

import com.taolife.common.utils.PageResult;
import com.taolife.wisdom.entity.Exercise;
import com.taolife.wisdom.param.ExerciseQueryParam;
import com.taolife.wisdom.param.ExerciseSaveParam;
import com.taolife.wisdom.vo.ExerciseDetailVO;
import com.taolife.wisdom.vo.ExerciseListVO;

/**
 * 运动项目服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IExerciseService {

    // ──── 管理后台 ────

    /**
     * 分页查询运动项目列表（管理后台）
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param category 分类（可选）
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    PageResult<Exercise> getExercisePage(Integer pageNo, Integer pageSize, Integer category, String keyword);

    /**
     * 获取运动项目详情（管理后台）
     *
     * @param id 运动项目ID
     * @return 运动项目详情
     */
    Exercise getExerciseDetail(String id);

    /**
     * 创建运动项目
     *
     * @param param 创建参数
     */
    void createExercise(ExerciseSaveParam param);

    /**
     * 修改运动项目信息
     *
     * @param id    运动项目ID
     * @param param 修改参数
     */
    void modifyExerciseInfo(String id, ExerciseSaveParam param);

    /**
     * 删除运动项目（逻辑删除）
     *
     * @param id 运动项目ID
     */
    void removeExercise(String id);

    /**
     * 修改运动项目状态（启用/禁用）
     *
     * @param id         运动项目ID
     * @param isDisabled 禁用标记（0：启用，1：禁用）
     */
    void modifyExerciseStatus(String id, Integer isDisabled);

    // ──── 用户端 ────

    /**
     * 用户端分页查询运动列表
     *
     * @param param 查询参数
     * @return 运动列表分页结果
     */
    PageResult<ExerciseListVO> getExerciseUserPage(ExerciseQueryParam param);

    /**
     * 用户端获取运动详情（含相关运动）
     *
     * @param id 运动项目ID
     * @return 运动详情VO
     */
    ExerciseDetailVO getExerciseUserDetail(String id);
}
