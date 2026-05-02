package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.MemberPlanPageParam;
import com.taolife.fee.param.MemberPlanSaveParam;
import com.taolife.fee.service.IMemberPlanService;
import com.taolife.fee.vo.MemberPlanVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员管理控制器
 * 负责处理管理端会员套餐相关的HTTP请求
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/member")
@RequiredArgsConstructor
public class MemberAdminController {

    private final IMemberPlanService memberPlanService;

    /**
     * 分页获取会员套餐列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping("/getMemberPlanPage")
    public ExceptionResult<PageResult<MemberPlanVO>> getMemberPlanPage(MemberPlanPageParam param) {
        log.info("分页查询会员套餐，参数：{}", param);
        return ExceptionResult.success(memberPlanService.getMemberPlanPage(param));
    }

    /**
     * 获取会员套餐详情
     *
     * @param id 套餐ID
     * @return 套餐详情
     */
    @GetMapping("/getMemberPlan")
    public ExceptionResult<MemberPlanVO> getMemberPlan(@RequestParam("id") String id) {
        log.info("获取会员套餐详情，id：{}", id);
        return ExceptionResult.success(memberPlanService.getMemberPlan(id));
    }

    /**
     * 获取启用的套餐列表
     *
     * @return 套餐列表
     */
    @GetMapping("/getEnabledPlans")
    public ExceptionResult<List<MemberPlanVO>> getEnabledPlans() {
        log.info("获取启用的套餐列表");
        return ExceptionResult.success(memberPlanService.getEnabledPlans());
    }

    /**
     * 创建会员套餐
     *
     * @param param 保存参数
     * @return 操作结果
     */
    @PostMapping("/createMemberPlan")
    public ExceptionResult<Void> createMemberPlan(@Valid @RequestBody MemberPlanSaveParam param) {
        log.info("创建会员套餐，参数：{}", param);
        memberPlanService.createMemberPlan(param);
        return ExceptionResult.success();
    }

    /**
     * 修改会员套餐
     *
     * @param id 套餐ID
     * @param param 保存参数
     * @return 操作结果
     */
    @PostMapping("/modifyMemberPlan")
    public ExceptionResult<Void> modifyMemberPlan(@RequestParam("id") String id,
                                                   @Valid @RequestBody MemberPlanSaveParam param) {
        log.info("修改会员套餐，id：{}，参数：{}", id, param);
        memberPlanService.modifyMemberPlan(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除会员套餐
     *
     * @param id 套餐ID
     * @return 操作结果
     */
    @PostMapping("/removeMemberPlan")
    public ExceptionResult<Void> removeMemberPlan(@RequestParam("id") String id) {
        log.info("删除会员套餐，id：{}", id);
        memberPlanService.removeMemberPlan(id);
        return ExceptionResult.success();
    }
}
