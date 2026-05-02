package com.taolife.fee.service;

import com.taolife.common.utils.PageResult;
import com.taolife.fee.vo.GrowthDetailVO;
import com.taolife.fee.vo.MemberGrowthRecordAdminVO;
import com.taolife.fee.vo.MemberGrowthStatusVO;

/**
 * 会员成长服务接口
 *
 * @author 文二
 * @date 2026-04-18
 */
public interface IMemberGrowthService {

    /**
     * 增加成长值
     *
     * @param accountId    账号ID
     * @param growth       成长值增量
     * @param source       来源枚举值
     * @param businessType 业务类型
     * @param businessId   业务记录ID
     * @param remark       备注
     */
    void addGrowth(String accountId, int growth, int source, String businessType, String businessId, String remark);

    /**
     * 发放每日登录成长值（仅会员）
     *
     * @param accountId 账号ID
     */
    void grantDailyGrowth(String accountId);

    /**
     * 获取会员成长状态
     *
     * @param accountId 账号ID
     * @return 成长状态VO
     */
    MemberGrowthStatusVO getGrowthStatus(String accountId);

    /**
     * 分页查询成长值记录
     *
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @param accountId 账号ID（可选）
     * @return 成长值记录分页结果
     */
    PageResult<MemberGrowthRecordAdminVO> getGrowthRecordPage(Integer pageNo, Integer pageSize, String accountId);

    /**
     * 管理员手动调整成长值
     *
     * @param accountId 账号ID
     * @param growth    调整值（正数增加，负数减少）
     * @param remark    备注
     */
    void adjustGrowth(String accountId, int growth, String remark);

    /**
     * 获取成长值详情（含等级定义列表）
     *
     * @param accountId 账号ID
     * @return 成长值详情VO
     */
    GrowthDetailVO getGrowthDetail(String accountId);
}
