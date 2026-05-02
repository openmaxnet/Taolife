package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.PointsRuleSaveParam;
import com.taolife.fee.service.IPointsRuleService;
import com.taolife.fee.vo.PointsRuleAdminVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分规则管理控制器（管理员）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/pointsRule")
@RequiredArgsConstructor
public class PointsRuleAdminController {

    private final IPointsRuleService pointsRuleService;

    /**
     * 分页查询积分规则
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 积分规则分页结果
     */
    @GetMapping("/getRulePage")
    public ExceptionResult<PageResult<PointsRuleAdminVO>> getRulePage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return ExceptionResult.success(pointsRuleService.getRulePage(pageNo, pageSize));
    }

    /**
     * 按 ID 查询积分规则详情
     *
     * @param id 规则ID
     * @return 积分规则详情
     */
    @GetMapping("/getRule")
    public ExceptionResult<PointsRuleAdminVO> getRule(@RequestParam("id") String id) {
        return ExceptionResult.success(pointsRuleService.getRuleForAdmin(id));
    }

    /**
     * 查询所有已启用的积分规则
     *
     * @return 已启用的积分规则列表
     */
    @GetMapping("/getEnabledRules")
    public ExceptionResult<List<PointsRuleAdminVO>> getEnabledRules() {
        return ExceptionResult.success(pointsRuleService.getEnabledRules());
    }

    /**
     * 创建积分规则
     *
     * @param param 积分规则保存参数
     * @return 新规则 ID
     */
    @PostMapping("/createRule")
    public ExceptionResult<String> createRule(@Valid @RequestBody PointsRuleSaveParam param) {
        return ExceptionResult.success(pointsRuleService.createRule(param));
    }

    /**
     * 修改积分规则
     *
     * @param id    规则ID
     * @param param 积分规则保存参数
     * @return 操作结果
     */
    @PostMapping("/modifyRule")
    public ExceptionResult<Void> modifyRule(@RequestParam("id") String id, @Valid @RequestBody PointsRuleSaveParam param) {
        pointsRuleService.modifyRule(id, param);
        return ExceptionResult.success();
    }

    /**
     * 删除积分规则
     *
     * @param id 规则ID
     * @return 操作结果
     */
    @PostMapping("/removeRule")
    public ExceptionResult<Void> removeRule(@RequestParam("id") String id) {
        pointsRuleService.removeRule(id);
        return ExceptionResult.success();
    }

    /**
     * 修改积分规则启用状态
     *
     * @param id        规则ID
     * @param isEnabled 是否启用：0-否，1-是
     * @return 操作结果
     */
    @PostMapping("/modifyRuleStatus")
    public ExceptionResult<Void> modifyRuleStatus(
            @RequestParam("id") String id,
            @RequestParam("isEnabled") Integer isEnabled) {
        pointsRuleService.modifyRuleStatus(id, isEnabled);
        return ExceptionResult.success();
    }
}
