package com.taolife.aichat.service;

import com.taolife.aichat.param.RemoveSessionAdminParam;
import com.taolife.aichat.param.SessionDetailAdminParam;
import com.taolife.aichat.param.SessionPageAdminParam;
import com.taolife.aichat.vo.SessionAdminVO;
import com.taolife.aichat.vo.SessionDetailAdminVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理员AI会话服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface IChatAdminService {

    /**
     * 分页查询会话列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<SessionAdminVO> getSessionPage(SessionPageAdminParam param);

    /**
     * 获取会话详情
     *
     * @param param 详情查询参数
     * @return 会话详情
     */
    SessionDetailAdminVO getSessionDetail(SessionDetailAdminParam param);

    /**
     * 删除会话
     *
     * @param param 删除参数
     */
    void removeSession(RemoveSessionAdminParam param);
}