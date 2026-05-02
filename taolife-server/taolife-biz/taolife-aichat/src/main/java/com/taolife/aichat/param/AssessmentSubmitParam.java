package com.taolife.aichat.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 体质测评提交参数
 *
 * @author 文二
 * @date 2026-03-22
 */
@Data
public class AssessmentSubmitParam {

    /**
     * 答案列表
     */
    @NotEmpty(message = "答案不能为空")
    private List<AnswerItem> answers;

    /**
     * 答案项
     */
    @Data
    public static class AnswerItem {
        /**
         * 题目ID
         */
        @NotNull(message = "题目ID不能为空")
        private String questionId;

        /**
         * 选项分值
         */
        @NotNull(message = "选项分值不能为空")
        private Integer optionValue;
    }
}
