package com.taolife.wisdom.enums;

import lombok.Getter;

/**
 * 体质编码枚举
 * 定义中医体质的中文分类名称与英文编码的映射关系
 *
 * @author 文二
 * @date 2026-04-03
 */
@Getter
public enum FitnessCodeEnum {

    /**
     * 平和质
     */
    PINGHE("平和质", "pinghe"),

    /**
     * 气虚质
     */
    QIXU("气虚质", "qixu"),

    /**
     * 阳虚质
     */
    YANGXU("阳虚质", "yangxu"),

    /**
     * 阴虚质
     */
    YINXU("阴虚质", "yinxu"),

    /**
     * 痰湿质
     */
    TANSHI("痰湿质", "tanshi"),

    /**
     * 湿热质
     */
    SHIRE("湿热质", "shire"),

    /**
     * 血瘀质
     */
    YUXU("血瘀质", "yuxu"),

    /**
     * 气郁质
     */
    QIYU("气郁质", "qiyu"),

    /**
     * 特禀质
     */
    TEBING("特禀质", "tebing"),

    /**
     * 基础信息
     * 用于收集用户基本信息（如年龄、性别等），不参与体质评分计算
     * 但会根据年龄和性别对体质得分进行倾向性加权调整
     */
    BASIC_INFO("基础信息", "basic_info");

    /**
     * 中文分类名称（对应数据库 tf_constitution_question 表的 category 字段）
     */
    private final String categoryName;

    /**
     * 英文编码（对应数据库 tf_constitution_type 表的 code 字段）
     */
    private final String code;

    /**
     * 构造函数
     *
     * @param categoryName 中文分类名称
     * @param code 英文编码
     */
    FitnessCodeEnum(String categoryName, String code) {
        this.categoryName = categoryName;
        this.code = code;
    }

    /**
     * 根据中文分类名称获取英文编码
     *
     * @param categoryName 中文分类名称
     * @return 英文编码，如果不存在则返回 null
     */
    public static String getCodeByCategoryName(String categoryName) {
        if (categoryName == null) {
            return null;
        }
        for (FitnessCodeEnum enumItem : values()) {
            if (enumItem.getCategoryName().equals(categoryName)) {
                return enumItem.getCode();
            }
        }
        return null;
    }

    /**
     * 根据英文编码获取中文分类名称
     *
     * @param code 英文编码
     * @return 中文分类名称，如果不存在则返回 null
     */
    public static String getCategoryNameByCode(String code) {
        if (code == null) {
            return null;
        }
        for (FitnessCodeEnum enumItem : values()) {
            if (enumItem.getCode().equals(code)) {
                return enumItem.getCategoryName();
            }
        }
        return null;
    }
}
