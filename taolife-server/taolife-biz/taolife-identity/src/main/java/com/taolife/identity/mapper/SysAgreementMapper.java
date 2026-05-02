package com.taolife.identity.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.identity.entity.SysAgreement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统协议与声明Mapper
 *
 * @author 文二
 * @date 2026-04-28
 */
@Mapper
public interface SysAgreementMapper extends BaseMapper<SysAgreement> {

    /**
     * 根据ID查询协议（过滤逻辑删除）
     *
     * @param id 协议ID
     * @return 协议实体
     */
    default SysAgreement selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SysAgreement::getId).eq(id)
                .and(SysAgreement::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 根据编码查询协议
     *
     * @param code 协议编码
     * @return 协议实体
     */
    default SysAgreement selectByCode(String code) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SysAgreement::getCode).eq(code)
                .and(SysAgreement::getIsDeleted).eq(0)
                .limit(1)
        );
    }
}
