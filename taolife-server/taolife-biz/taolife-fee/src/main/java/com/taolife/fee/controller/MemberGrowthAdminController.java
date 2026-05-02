package com.taolife.fee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.param.GrowthLevelPageParam;
import com.taolife.fee.param.GrowthLevelSaveParam;
import com.taolife.fee.service.IGrowthLevelAdminService;
import com.taolife.fee.service.IMemberGrowthService;
import com.taolife.fee.vo.GrowthLevelAdminVO;
import com.taolife.fee.vo.MemberGrowthRecordAdminVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 会员成长管理控制器（管理员）
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
@RestController
@RequestMapping("/api/fee/admin/growth")
@RequiredArgsConstructor
public class MemberGrowthAdminController {

    private final IMemberGrowthService memberGrowthService;
    private final IGrowthLevelAdminService growthLevelAdminService;

    /**
     * 分页查询成长值记录
     *
     * @param pageNo    页码
     * @param pageSize  每页条数
     * @param accountId 账号ID（可选）
     * @return 成长值记录分页结果
     */
    @GetMapping("/getGrowthRecordPage")
    public ExceptionResult<PageResult<MemberGrowthRecordAdminVO>> getGrowthRecordPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "accountId", required = false) String accountId) {
        return ExceptionResult.success(memberGrowthService.getGrowthRecordPage(pageNo, pageSize, accountId));
    }

    /**
     * 管理员手动调整用户成长值
     *
     * @param accountId 账号ID
     * @param growth    调整值
     * @param remark    调整原因
     * @return 操作结果
     */
    @PostMapping("/adjustGrowth")
    public ExceptionResult<Void> adjustGrowth(
            @RequestParam("accountId") String accountId,
            @RequestParam("growth") Integer growth,
            @RequestParam(value = "remark", required = false) String remark) {
        memberGrowthService.adjustGrowth(accountId, growth, remark);
        return ExceptionResult.success();
    }

    /**
     * 分页查询成长等级配置
     *
     * @param param 分页查询参数
     * @return 成长等级分页结果
     */
    @GetMapping("/getGrowthLevelPage")
    public ExceptionResult<PageResult<GrowthLevelAdminVO>> getGrowthLevelPage(GrowthLevelPageParam param) {
        return ExceptionResult.success(growthLevelAdminService.getGrowthLevelPage(param));
    }

    /**
     * 查询成长等级详情
     *
     * @param id 等级ID
     * @return 成长等级详情
     */
    @GetMapping("/getGrowthLevelDetail")
    public ExceptionResult<GrowthLevelAdminVO> getGrowthLevelDetail(@RequestParam("id") String id) {
        return ExceptionResult.success(growthLevelAdminService.getGrowthLevelDetail(id));
    }

    /**
     * 创建成长等级
     *
     * @param param 等级保存参数
     * @return 操作结果
     */
    @PostMapping("/createGrowthLevel")
    public ExceptionResult<Void> createGrowthLevel(@Valid @RequestBody GrowthLevelSaveParam param) {
        growthLevelAdminService.createGrowthLevel(param);
        return ExceptionResult.success();
    }

    /**
     * 修改成长等级信息
     *
     * @param id    等级ID
     * @param param 等级保存参数
     * @return 操作结果
     */
    @PostMapping("/modifyGrowthLevelInfo")
    public ExceptionResult<Void> modifyGrowthLevelInfo(@RequestParam("id") String id,
                                                        @Valid @RequestBody GrowthLevelSaveParam param) {
        growthLevelAdminService.modifyGrowthLevelInfo(id, param);
        return ExceptionResult.success();
    }

    /**
     * 修改成长等级启用状态
     *
     * @param id        等级ID
     * @param isEnabled 是否启用：0-否，1-是
     * @return 操作结果
     */
    @PostMapping("/modifyGrowthLevelStatus")
    public ExceptionResult<Void> modifyGrowthLevelStatus(@RequestParam("id") String id,
                                                           @RequestParam("isEnabled") Integer isEnabled) {
        growthLevelAdminService.modifyGrowthLevelStatus(id, isEnabled);
        return ExceptionResult.success();
    }

    /**
     * 删除成长等级
     *
     * @param id 等级ID
     * @return 操作结果
     */
    @PostMapping("/removeGrowthLevel")
    public ExceptionResult<Void> removeGrowthLevel(@RequestParam("id") String id) {
        growthLevelAdminService.removeGrowthLevel(id);
        return ExceptionResult.success();
    }
}
