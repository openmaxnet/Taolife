package com.taolife.identity.controller;

import java.util.Map;

import com.taolife.common.annotation.AuthSkip;
import com.taolife.common.security.RsaKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 安全相关控制器
 * 提供加密公钥分发等接口，仅在加密功能启用时注册
 */
@RestController
@RequestMapping("/api/common/security")
@RequiredArgsConstructor
@ConditionalOnBean(RsaKeyManager.class)
public class SecurityController {

    private final RsaKeyManager rsaKeyManager;

    /**
     * 获取服务端 RSA 公钥
     * 客户端使用该公钥加密 AES 密钥，实现混合加密
     *
     * @return 包含 Base64 编码公钥的响应
     */
    @AuthSkip
    @GetMapping("/publicKey")
    public Map<String, String> getPublicKey() {
        return Map.of("publicKey", rsaKeyManager.getPublicKeyBase64());
    }
}
