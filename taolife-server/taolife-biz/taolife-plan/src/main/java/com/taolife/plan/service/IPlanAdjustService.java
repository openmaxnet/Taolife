package com.taolife.plan.service;

import com.taolife.common.utils.PageResult;
import com.taolife.plan.param.AiAdjustParam;
import com.taolife.plan.param.BatchAiAdjustParam;
import com.taolife.plan.param.PlanAdjustRecordQueryParam;
import com.taolife.plan.param.ManualAdjustmentParam;
import com.taolife.plan.vo.AdjustmentRecordVO;
import com.taolife.plan.vo.AiAdjustResultVO;
import com.taolife.plan.vo.BatchAiAdjustResultVO;

import java.util.List;

/**
 * 方案调整服务接口
 * 定义方案调整相关的业务操作
 *
 * @author 文二
 * @date 2026-04-06
 */
public interface IPlanAdjustService {

    /**
     * 手动调整方案
     *
     * @param accountId 账号ID
     * @param param     手动调整参数
     */
    void manualAdjust(String accountId, ManualAdjustmentParam param);

    /**
     * 分页查询调整记录
     *
     * @param userPlanId 用户方案ID
     * @param param      查询参数
     * @return 调整记录分页结果
     */
    PageResult<AdjustmentRecordVO> getAdjustmentRecordPage(String userPlanId, PlanAdjustRecordQueryParam param);

    /**
     * 获取智能调整推荐标签
     *
     * @param accountId  账号ID
     * @param userPlanId 用户方案ID
     * @param planType   方案类型
     * @return 推荐标签列表
     */
    List<String> getAdjustmentSuggestions(String accountId, String userPlanId, Integer planType);

    /**
     * AI调整方案（单条）
     *
     * @param accountId 账号ID
     * @param param     调整参数
     * @return 调整结果（含预览）
     */
    AiAdjustResultVO aiAdjust(String accountId, AiAdjustParam param);

    /**
     * AI批量调整方案
     *
     * @param accountId 账号ID
     * @param param     批量调整参数
     * @return 批量调整结果
     */
    BatchAiAdjustResultVO batchAiAdjust(String accountId, BatchAiAdjustParam param);

    /**
     * 确认AI调整（单条）
     *
     * @param accountId     账号ID
     * @param adjustmentId  调整记录ID
     */
    void confirmAiAdjust(String accountId, String adjustmentId);

    /**
     * 批量确认AI调整
     *
     * @param accountId      账号ID
     * @param adjustmentIds  调整记录ID列表
     */
    void batchConfirmAiAdjust(String accountId, List<String> adjustmentIds);
}
