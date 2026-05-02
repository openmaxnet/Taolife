package com.taolife.identity.service;

import com.taolife.identity.vo.SysAgreementVO;

/**
 * 系统协议与声明服务接口
 *
 * @author 文二
 * @date 2026-04-28
 */
public interface ISysAgreementService {

    /**
     * 根据编码获取协议文档
     *
     * @param code 协议编码
     * @return 协议内容VO
     */
    SysAgreementVO getByCode(String code);
}
