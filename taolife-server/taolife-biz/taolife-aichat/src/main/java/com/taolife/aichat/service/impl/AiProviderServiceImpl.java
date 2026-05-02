package com.taolife.aichat.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.aichat.entity.AiProvider;
import com.taolife.aichat.mapper.AiProviderMapper;
import com.taolife.aichat.param.AiProviderPageParam;
import com.taolife.aichat.param.AiProviderSaveParam;
import com.taolife.aichat.param.AiRemoveParam;
import com.taolife.aichat.param.AiStatusParam;
import com.taolife.aichat.service.IAiProviderService;
import com.taolife.aichat.vo.AiProviderVO;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.properties.EncryptionProperties;
import com.taolife.common.utils.AesEncryptionUtil;
import com.taolife.common.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * AI模型厂商服务实现
 *
 * @author 文二
 * @date 2026-04-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiProviderServiceImpl implements IAiProviderService {

    private final AiProviderMapper aiProviderMapper;
    private final EncryptionProperties encryptionProperties;

    /**
     * 分页查询厂商列表
     *
     * @param param 分页查询参数
     * @return 厂商分页结果
     */
    @Override
    public PageResult<AiProviderVO> getProviderPage(AiProviderPageParam param) {
        Page<AiProvider> page = aiProviderMapper.selectPageByTypeAndKeyword(
                param.getPageNo(), param.getPageSize(), param.getProviderType(), param.getKeyword());

        PageResult<AiProvider> pageResult = PageResult.of(page);
        PageResult<AiProviderVO> result = new PageResult<>();
        result.setList(pageResult.getList().stream().map(this::convertToVO).toList());
        result.setPageNo(pageResult.getPageNo());
        result.setPageSize(pageResult.getPageSize());
        result.setTotalPage(pageResult.getTotalPage());
        result.setTotalRow(pageResult.getTotalRow());
        return result;
    }

    /**
     * 获取厂商详情
     *
     * @param id 厂商ID
     * @return 厂商详情VO
     */
    @Override
    public AiProviderVO getProviderDetail(String id) {
        AiProvider provider = aiProviderMapper.selectOneById(id);
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "厂商不存在");
        }
        return convertToVO(provider);
    }

    /**
     * 创建厂商（API Key自动加密）
     *
     * @param param 创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProvider(AiProviderSaveParam param) {
        AiProvider provider = new AiProvider();
        provider.setProviderCode(param.getProviderCode());
        provider.setProviderName(param.getProviderName());
        provider.setProviderType(param.getProviderType());
        provider.setApiEndpoint(param.getApiEndpoint());

        // 如果启用加密，则加密API Key
        boolean shouldEncrypt = encryptionProperties.isEnabled() && (param.getIsEncrypted() == null || param.getIsEncrypted() == 1);
        if (shouldEncrypt && param.getApiKey() != null && !param.getApiKey().isEmpty()) {
            provider.setApiKey(AesEncryptionUtil.encrypt(param.getApiKey()));
            provider.setIsEncrypted(1);
        } else {
            provider.setApiKey(param.getApiKey());
            provider.setIsEncrypted(param.getIsEncrypted() != null ? param.getIsEncrypted() : 0);
        }

        provider.setIsDefault(param.getIsDefault() != null ? param.getIsDefault() : 0);
        // 设置为默认时，先取消同类型其他默认
        if (provider.getIsDefault() == 1) {
            aiProviderMapper.resetDefaultByType(provider.getProviderType());
        }
        provider.setPriority(param.getPriority() != null ? param.getPriority() : 0);
        provider.setStatus(param.getStatus() != null ? param.getStatus() : 1);
        provider.setDescription(param.getDescription());
        provider.setConfigJson(param.getConfigJson());
        provider.setCreateTime(LocalDateTime.now());
        provider.setUpdateTime(LocalDateTime.now());
        aiProviderMapper.insert(provider);
    }

    /**
     * 修改厂商信息
     *
     * @param param 修改参数（含ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyProviderInfo(AiProviderSaveParam param) {
        if (param.getId() == null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "缺少ID参数");
        }
        AiProvider provider = aiProviderMapper.selectOneById(param.getId());
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "厂商不存在");
        }
        provider.setProviderCode(param.getProviderCode());
        provider.setProviderName(param.getProviderName());
        provider.setProviderType(param.getProviderType());
        provider.setApiEndpoint(param.getApiEndpoint());
        if (param.getApiKey() != null && !param.getApiKey().isEmpty()) {
            // 如果启用加密，则加密API Key
            boolean shouldEncrypt = encryptionProperties.isEnabled() && (param.getIsEncrypted() == null || param.getIsEncrypted() == 1 || provider.getIsEncrypted() == 1);
            if (shouldEncrypt) {
                provider.setApiKey(AesEncryptionUtil.encrypt(param.getApiKey()));
                provider.setIsEncrypted(1);
            } else {
                provider.setApiKey(param.getApiKey());
            }
        }
        if (param.getIsEncrypted() != null) {
            provider.setIsEncrypted(param.getIsEncrypted());
        }
        if (param.getIsDefault() != null) {
            // 设置为默认时，先取消同类型其他默认
            if (param.getIsDefault() == 1) {
                aiProviderMapper.resetDefaultByType(provider.getProviderType());
            }
            provider.setIsDefault(param.getIsDefault());
        }
        if (param.getPriority() != null) {
            provider.setPriority(param.getPriority());
        }
        if (param.getStatus() != null) {
            provider.setStatus(param.getStatus());
        }
        if (param.getDescription() != null) {
            provider.setDescription(param.getDescription());
        }
        if (param.getConfigJson() != null) {
            provider.setConfigJson(param.getConfigJson());
        }
        provider.setUpdateTime(LocalDateTime.now());
        aiProviderMapper.update(provider);
    }

    /**
     * 删除厂商
     *
     * @param param 删除参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProvider(AiRemoveParam param) {
        AiProvider provider = aiProviderMapper.selectOneById(param.getId());
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "厂商不存在");
        }
        aiProviderMapper.deleteById(param.getId());
    }

    /**
     * 修改厂商状态
     *
     * @param param 状态修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyProviderStatus(AiStatusParam param) {
        AiProvider provider = aiProviderMapper.selectOneById(param.getId());
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "厂商不存在");
        }
        provider.setStatus(param.getStatus());
        provider.setUpdateTime(LocalDateTime.now());
        aiProviderMapper.update(provider);
    }

    /**
     * 根据类型获取可用的厂商列表
     *
     * @param providerType 厂商类型：chat/embedding
     * @return 厂商列表
     */
    @Override
    public java.util.List<AiProviderVO> getEnabledProvidersByType(String providerType) {
        return aiProviderMapper.selectByProviderType(providerType)
                .stream().map(this::convertToVO).toList();
    }

    /**
     * 获取解密后的API Key
     *
     * @param id 厂商ID
     * @return 解密后的API Key
     */
    @Override
    public String getDecryptedApiKey(String id) {
        AiProvider provider = aiProviderMapper.selectOneById(id);
        if (provider == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "厂商不存在");
        }
        String apiKey = provider.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return apiKey;
        }
        // 如果启用加密且标记为加密，则解密
        if (encryptionProperties.isEnabled() && provider.getIsEncrypted() != null && provider.getIsEncrypted() == 1) {
            try {
                return AesEncryptionUtil.decrypt(apiKey);
            } catch (Exception e) {
                log.error("API Key解密失败，providerId: {}", id, e);
                throw new BusinessException(ExceptionCode.DATA_DECRYPT_ERROR);
            }
        }
        return apiKey;
    }

    /**
     * 厂商实体转VO（API Key脱敏）
     *
     * @param provider 厂商实体
     * @return 厂商VO
     */
    private AiProviderVO convertToVO(AiProvider provider) {
        AiProviderVO vo = new AiProviderVO();
        vo.setId(provider.getId());
        vo.setProviderCode(provider.getProviderCode());
        vo.setProviderName(provider.getProviderName());
        vo.setProviderType(provider.getProviderType());
        vo.setApiEndpoint(provider.getApiEndpoint());
        vo.setApiKeyMasked(maskApiKey(provider.getApiKey()));
        vo.setIsEncrypted(provider.getIsEncrypted());
        vo.setIsDefault(provider.getIsDefault());
        vo.setPriority(provider.getPriority());
        vo.setStatus(provider.getStatus());
        vo.setDescription(provider.getDescription());
        vo.setConfigJson(provider.getConfigJson());
        vo.setCreateTime(provider.getCreateTime());
        vo.setUpdateTime(provider.getUpdateTime());
        return vo;
    }

    /**
     * 脱敏API Key（显示首尾4位）
     *
     * @param apiKey 原始API Key
     * @return 脱敏后的Key
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "******";
        }
        return apiKey.substring(0, 4) + "******" + apiKey.substring(apiKey.length() - 4);
    }
}
