package com.taolife.fee.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.taolife.common.utils.PageResult;
import com.taolife.fee.entity.CheckinRecord;
import com.taolife.fee.mapper.CheckinRecordMapper;
import com.taolife.fee.service.ICheckinAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 签到管理服务实现类（后台管理）
 *
 * @author 文二
 * @date 2026-04-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinAdminServiceImpl implements ICheckinAdminService {

    private final CheckinRecordMapper checkinRecordMapper;

    /**
     * 分页查询签到记录（管理员）
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 签到记录分页结果
     */
    @Override
    public PageResult<CheckinRecord> getCheckinRecordPage(Integer pageNo, Integer pageSize) {
        Page<CheckinRecord> page = checkinRecordMapper.selectAdminPage(new Page<>(pageNo, pageSize));
        return new PageResult<>(page.getRecords(), pageNo, pageSize, page.getTotalRow());
    }
}
