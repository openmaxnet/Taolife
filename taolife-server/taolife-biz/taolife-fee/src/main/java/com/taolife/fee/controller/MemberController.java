package com.taolife.fee.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.service.IMemberPlanService;
import com.taolife.fee.service.IMemberService;
import com.taolife.fee.service.IMemberGrowthService;
import com.taolife.fee.vo.GrowthDetailVO;
import com.taolife.fee.vo.MemberGrowthRecordAdminVO;
import com.taolife.fee.vo.MemberGrowthStatusVO;
import com.taolife.fee.vo.MemberPlanVO;
import com.taolife.fee.vo.MemberStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员信息控制器（用户端）
 *
 * @author 文二
 * @date 2026-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/fee/member")
@RequiredArgsConstructor
public class MemberController {

    private final IMemberService memberService;
    private final IMemberPlanService memberPlanService;
    private final IMemberGrowthService memberGrowthService;

    /**
     * 获取当前登录用户的会员状态
     *
     * @return 会员状态信息
     */
    @GetMapping("/getMemberStatus")
    public ExceptionResult<MemberStatusVO> getMemberStatus() {
        String accountId = UserContext.getAccountId();
        log.info("获取会员状态，accountId：{}", accountId);
        return ExceptionResult.success(memberService.getMemberStatus(accountId));
    }

    /**
     * 获取所有已启用的会员套餐列表
     *
     * @return 会员套餐列表
     */
    @GetMapping("/getEnabledPlans")
    public ExceptionResult<List<MemberPlanVO>> getEnabledPlans() {
        return ExceptionResult.success(memberPlanService.getEnabledPlans());
    }

    /**
     * 获取当前登录用户的成长值状态
     *
     * @return 成长值状态信息
     */
    @GetMapping("/getGrowthStatus")
    public ExceptionResult<MemberGrowthStatusVO> getGrowthStatus() {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(memberGrowthService.getGrowthStatus(accountId));
    }

    /**
     * 分页查询当前登录用户的成长值变动记录
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 成长值记录分页结果
     */
    @GetMapping("/getGrowthRecords")
    public ExceptionResult<PageResult<MemberGrowthRecordAdminVO>> getGrowthRecords(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(memberGrowthService.getGrowthRecordPage(pageNo, pageSize, accountId));
    }

    /**
     * 获取当前登录用户的成长值详情（含等级定义列表）
     *
     * @return 成长值详情
     */
    @GetMapping("/getGrowthDetail")
    public ExceptionResult<GrowthDetailVO> getGrowthDetail() {
        String accountId = UserContext.getAccountId();
        return ExceptionResult.success(memberGrowthService.getGrowthDetail(accountId));
    }
}
