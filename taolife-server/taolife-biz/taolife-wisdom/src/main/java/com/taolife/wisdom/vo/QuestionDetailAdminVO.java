package com.taolife.wisdom.vo;

import lombok.Data;

import java.util.List;

/**
 * 管理员体质问卷题目详情VO
 * 包含题目信息和选项列表
 *
 * @author 文二
 * @date 2026-04-12
 */
@Data
public class QuestionDetailAdminVO {

    /**
     * 题目基本信息
     */
    private FitnessQuestionAdminVO question;

    /**
     * 选项列表
     */
    private List<OptionAdminVO> options;
}