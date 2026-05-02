package com.taolife.identity.param;

import lombok.Data;

/**
 * 生活习惯偏好参数
 */
@Data
public class LifestylePreferenceParam {

    /** 就寝时间 如"23:00" */
    private String sleepTime;

    /** 起床时间 如"07:00" */
    private String wakeTime;

    /** 睡眠质量：1优 2良 3一般 4差 */
    private Integer sleepQuality;

    /** 压力水平：1低 2中 3高 */
    private Integer stressLevel;

    /** 吸烟状态：0从不 1已戒 2吸烟 */
    private Integer smokingStatus;

    /** 饮酒状态：0从不 1偶尔 2经常 */
    private Integer drinkingStatus;

    /** 职业 */
    private String occupation;
}
