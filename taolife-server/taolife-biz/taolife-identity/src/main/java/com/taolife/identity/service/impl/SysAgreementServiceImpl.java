package com.taolife.identity.service.impl;

import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.identity.entity.SysAgreement;
import com.taolife.identity.mapper.SysAgreementMapper;
import com.taolife.identity.service.ISysAgreementService;
import com.taolife.identity.vo.SysAgreementVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统协议与声明服务实现
 *
 * @author 文二
 * @date 2026-04-28
 */
@Service
@RequiredArgsConstructor
public class SysAgreementServiceImpl implements ISysAgreementService {

    private final SysAgreementMapper agreementMapper;

    /**
     * 根据编码获取协议
     * 通过协议编码查询公开的协议内容
     *
     * @param code 协议编码
     * @return 协议内容VO
     */
    @Override
    public SysAgreementVO getByCode(String code) {
        SysAgreement entity = agreementMapper.selectByCode(code);
        if (entity == null) {
            throw new BusinessException(ExceptionCode.BUSINESS_ERROR, "文档不存在");
        }
        SysAgreementVO vo = new SysAgreementVO();
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setVersion(entity.getVersion());
        return vo;
    }
}
