package com.taolife.common.utils;

import java.util.UUID;

import com.mybatisflex.core.keygen.IKeyGenerator;

/**
 * UUID主键生成器
 */
public class UUIDKeyGeneratorUtil implements IKeyGenerator {
    @Override
    public Object generate(Object entity, String keyColumn) {
        UUID uuid = UUID.randomUUID();
        // 只使用UUID的高64位，转换为16位的十六进制字符串
        return String.format("%016x", uuid.getMostSignificantBits());
    }
}