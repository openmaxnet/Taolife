package com.taolife.aichat.service;

import com.taolife.aichat.param.*;
import com.taolife.aichat.param.ImportAdminParam;
import com.taolife.aichat.vo.SensitiveWordAdminVO;
import com.taolife.aichat.vo.ImportResultVO;
import com.taolife.common.utils.PageResult;

/**
 * 管理员敏感词服务接口
 *
 * @author 文二
 * @date 2026-04-12
 */
public interface ISensitiveWordAdminService {

    /**
     * 分页查询敏感词列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<SensitiveWordAdminVO> getSensitiveWordPage(SensitiveWordPageAdminParam param);

    /**
     * 获取敏感词详情
     *
     * @param param 详情查询参数
     * @return 敏感词详情
     */
    SensitiveWordAdminVO getSensitiveWordDetail(SensitiveWordDetailAdminParam param);

    /**
     * 创建敏感词
     *
     * @param param 创建参数
     */
    void createSensitiveWord(SensitiveWordSaveAdminParam param);

    /**
     * 修改敏感词
     *
     * @param param 修改参数
     */
    void modifySensitiveWordInfo(SensitiveWordSaveAdminParam param);

    /**
     * 删除敏感词
     *
     * @param param 删除参数
     */
    void removeSensitiveWord(RemoveSensitiveWordAdminParam param);

    /**
     * 修改敏感词状态
     *
     * @param param 状态修改参数
     */
    void modifySensitiveWordStatus(ModifySensitiveWordStatusAdminParam param);

    /**
     * 批量导入敏感词
     *
     * @param param 导入参数
     * @return 导入结果
     */
    ImportResultVO importSensitiveWords(ImportAdminParam param);
}