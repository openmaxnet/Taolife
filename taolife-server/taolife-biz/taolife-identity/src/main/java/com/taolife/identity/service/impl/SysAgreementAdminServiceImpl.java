package com.taolife.identity.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import com.taolife.identity.entity.SysAgreement;
import com.taolife.identity.mapper.SysAgreementMapper;
import com.taolife.identity.param.SysAgreementSaveParam;
import com.taolife.identity.service.ISysAgreementAdminService;
import com.taolife.identity.vo.SysAgreementAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.taolife.identity.entity.table.SysAgreementTableDef.SYS_AGREEMENT;

/**
 * 系统协议管理服务实现
 *
 * @author 文二
 * @date 2026-04-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysAgreementAdminServiceImpl implements ISysAgreementAdminService {

    private final SysAgreementMapper agreementMapper;

    /**
     * 分页查询协议列表
     * 根据关键词分页查询系统协议
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @param keyword  关键词（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<SysAgreementAdminVO> getAgreementPage(int pageNo, int pageSize, String keyword) {
        QueryWrapper query = QueryWrapper.create()
                .select(SYS_AGREEMENT.ID, SYS_AGREEMENT.CODE, SYS_AGREEMENT.TITLE,
                        SYS_AGREEMENT.VERSION, SYS_AGREEMENT.UPDATE_TIME)
                .orderBy(SYS_AGREEMENT.UPDATE_TIME, false);

        if (keyword != null && !keyword.isBlank()) {
            query.where(SYS_AGREEMENT.CODE.like(keyword).or(SYS_AGREEMENT.TITLE.like(keyword)));
        }

        Page<SysAgreement> page = agreementMapper.paginate(
                new Page<>(pageNo, pageSize), query);

        return new PageResult<>(
                page.getRecords().stream().map(this::convertToVO).toList(),
                pageNo, pageSize, page.getTotalRow());
    }

    /**
     * 获取协议详情
     * 根据ID查询系统协议详细内容
     *
     * @param id 协议ID
     * @return 协议实体
     */
    @Override
    public SysAgreement getAgreementDetail(String id) {
        SysAgreement agreement = agreementMapper.selectById(id);
        if (agreement == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文档不存在");
        }
        return agreement;
    }

    /**
     * 创建协议
     * 新增系统协议（检查编码唯一性）
     *
     * @param param 协议创建参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAgreement(SysAgreementSaveParam param) {
        SysAgreement existing = agreementMapper.selectByCode(param.getCode());
        if (existing != null) {
            throw new BusinessException(ExceptionCode.PARAM_ERROR, "文档标识已存在: " + param.getCode());
        }

        SysAgreement agreement = new SysAgreement();
        agreement.setCode(param.getCode());
        agreement.setTitle(param.getTitle());
        agreement.setContent(param.getContent());
        agreement.setVersion(1);
        agreementMapper.insert(agreement);
        log.info("创建系统协议：code={}", param.getCode());
    }

    /**
     * 修改协议信息
     * 根据ID修改协议内容并自增版本号
     *
     * @param id    协议ID
     * @param param 协议修改参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyAgreementInfo(String id, SysAgreementSaveParam param) {
        SysAgreement agreement = agreementMapper.selectById(id);
        if (agreement == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文档不存在");
        }

        // code 变更时检查唯一性
        if (!agreement.getCode().equals(param.getCode())) {
            SysAgreement existing = agreementMapper.selectByCode(param.getCode());
            if (existing != null) {
                throw new BusinessException(ExceptionCode.PARAM_ERROR, "文档标识已存在: " + param.getCode());
            }
        }

        agreement.setCode(param.getCode());
        agreement.setTitle(param.getTitle());
        agreement.setContent(param.getContent());
        agreement.setVersion(agreement.getVersion() + 1);
        agreement.setUpdateTime(LocalDateTime.now());
        agreementMapper.update(agreement);
        log.info("修改系统协议：id={}，code={}", id, param.getCode());
    }

    /**
     * 删除协议
     * 根据ID删除系统协议（物理删除）
     *
     * @param id 协议ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAgreement(String id) {
        SysAgreement agreement = agreementMapper.selectById(id);
        if (agreement == null) {
            throw new BusinessException(ExceptionCode.DATA_NOT_FOUND, "文档不存在");
        }
        agreementMapper.deleteById(id);
        log.info("删除系统协议：id={}，code={}", id, agreement.getCode());
    }

    private SysAgreementAdminVO convertToVO(SysAgreement agreement) {
        SysAgreementAdminVO vo = new SysAgreementAdminVO();
        vo.setId(agreement.getId());
        vo.setCode(agreement.getCode());
        vo.setTitle(agreement.getTitle());
        vo.setVersion(agreement.getVersion());
        vo.setUpdateTime(agreement.getUpdateTime());
        return vo;
    }
}
