package com.taolife.fee.controller;

import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.security.UserContext;
import com.taolife.fee.param.AdActionParam;
import com.taolife.fee.service.IAdConfigService;
import com.taolife.fee.vo.AdConfigVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告配置控制器
 *
 * @author 文二
 * @date 2026-04-18
 */
@Slf4j
@RestController
@RequestMapping("/api/fee/ad")
@RequiredArgsConstructor
public class AdConfigController {

    private final IAdConfigService adConfigService;

    /**
     * 获取当前用户可见的广告配置列表
     * 根据用户会员状态过滤广告（免费用户限制等）
     *
     * @return 广告配置列表
     */
    @GetMapping("/getAdConfigs")
    public ExceptionResult<List<AdConfigVO>> getAdConfigs() {
        String accountId = UserContext.getAccountId();
        List<AdConfigVO> result = adConfigService.getVisibleAdConfigs(accountId);
        return ExceptionResult.success(result);
    }

    /**
     * 上报广告动作（展示、点击、关闭、获得奖励）
     *
     * @param param 广告动作参数
     * @return 操作结果
     */
    @PostMapping("/reportAdAction")
    public ExceptionResult<Void> reportAdAction(@Valid @RequestBody AdActionParam param) {
        String accountId = UserContext.getAccountId();
        adConfigService.reportAdAction(accountId, param.getAdConfigKey(), param.getAction(), param.getDuration());
        return ExceptionResult.success(null);
    }
}
