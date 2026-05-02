package com.taolife.fee.service;

import com.taolife.fee.vo.QuotaStatusVO;

/**
 * 配额管理服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IQuotaService {

    /**
     * 检查并扣减AI问答配额
     *
     * @param accountId 账号ID
     * @throws com.taolife.common.exception.BusinessException 配额已用完时抛出
     */
    void checkAndDecrementAiQuota(String accountId);

    /**
     * 获取AI问答配额状态
     */
    QuotaStatusVO getAiQuotaStatus(String accountId);

    /**
     * 增加AI问答配额（通过积分兑换或广告奖励）
     */
    void incrementAiQuota(String accountId, int count);

    /**
     * 检查体质评估配额
     */
    void checkAssessmentQuota(String accountId);

    /**
     * 增加体质评估配额
     */
    void incrementAssessmentQuota(String accountId, int count);
}
