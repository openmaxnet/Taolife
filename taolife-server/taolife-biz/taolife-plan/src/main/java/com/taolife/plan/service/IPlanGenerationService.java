package com.taolife.plan.service;

import com.taolife.plan.vo.GeneratePlanVO;
import com.taolife.plan.vo.GenerationStatusVO;

/**
 * 方案生成服务接口
 * 定义健康方案异步生成相关的业务操作
 *
 * @author 文二
 * @date 2026-04-06
 */
public interface IPlanGenerationService {

    /**
     * 提交方案生成任务
     * 异步生成个性化健康方案（体质、季节、偏好等由后端自动获取）
     *
     * @param accountId 账号ID
     * @param cycleDays 方案周期天数，默认7天
     * @return 生成结果信息
     */
    GeneratePlanVO submitGenerationTask(String accountId, int cycleDays);

    /**
     * 获取方案生成状态
     * 根据任务ID查询方案生成的进度和结果
     *
     * @param taskId 任务ID
     * @return 生成状态VO
     */
    GenerationStatusVO getGenerationStatus(String taskId);
}
