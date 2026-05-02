package com.taolife.identity.controller;

import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.exception.ExceptionResult;
import com.taolife.common.properties.CosProperties;
import com.taolife.common.security.UserContext;
import com.taolife.common.utils.CosStsUtil;
import com.taolife.common.vo.CosCredentialVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
/**
 * 文件上传控制器
 * 提供COS临时凭证，用于前端直传文件到腾讯云对象存储
 *
 * @author 文二
 * @date 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class FileUploadController {

    private final CosProperties cosProperties;

    /**
     * 获取COS临时凭证（用于前端直传COS）
     *
     * @param fileName 文件名（带扩展名）
     * @return COS临时凭证信息
     */
    @GetMapping("/cos/credential")
    public ExceptionResult<CosCredentialVO> getCosCredential(@RequestParam String fileName) {
        // 获取当前登录用户ID
        String userId = UserContext.getAccountId();
        if (userId == null) {
            return ExceptionResult.failed(ExceptionCode.UNAUTHORIZED);
        }

        log.debug("获取COS凭证, fileName: {}, userId: {}", fileName, userId);

        // 获取STS临时凭证
        CosStsUtil.StsCredential stsCredential = CosStsUtil.getCredential(cosProperties, fileName, userId);

        // 生成COS对象路径
        String cosKey = CosStsUtil.generateCosKey(cosProperties, fileName, userId);

        // 构建响应VO
        CosCredentialVO vo = new CosCredentialVO();
        vo.setTmpSecretId(stsCredential.getTmpSecretId());
        vo.setTmpSecretKey(stsCredential.getTmpSecretKey());
        vo.setSessionToken(stsCredential.getSessionToken());
        vo.setCosKey(cosKey);
        vo.setDomain(cosProperties.getDomain());
        vo.setBucketName(cosProperties.getBucketName());
        vo.setRegion(cosProperties.getRegion());

        log.info("COS凭证获取成功, userId: {}, cosKey: {}", userId, cosKey);

        return ExceptionResult.success(vo);
    }

}