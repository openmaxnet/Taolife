package com.taolife.aichat.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * 问题规则关键词实体
 */
@Data
@Table("tl_ai_chat_rule_keyword")
public class ChatRuleKeyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "Flex-UUID")
    private String id;

    private String ruleId;

    private String keyword;

    private Integer sortOrder;
}
