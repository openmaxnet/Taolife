package com.taolife.identity.param;

import lombok.Data;

/**
 * AI偏好参数
 */
@Data
public class AiPreferenceParam {

    /** AI回复风格：1专业 2亲切 3简洁 */
    private Integer aiTonePreference;

    /** AI详细程度：1简要 2适中 3详细 */
    private Integer aiDetailLevel;
}
