package com.taolife.aichat.service.impl;

import com.taolife.aichat.enums.AiCacheExpireEnum;
import com.taolife.aichat.enums.AiRedisKeyEnum;
import com.taolife.aichat.service.IStreamCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 流式消息缓存服务实现
 * 使用 Redis 缓存流式消息内容，避免频繁操作数据库
 *
 * @author 文二
 * @date 2026-03-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamCacheServiceImpl implements IStreamCacheService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存流式消息初始内容
     * 将流式消息的初始内容存入Redis，并设置过期时间
     *
     * @param sessionId 会话ID
     * @param accountId 用户账户ID
     * @param messageId 消息ID
     * @param initialContent 初始内容
     */
    @Override
    public void cacheStreamMessage(String sessionId, String accountId, String messageId, String initialContent) {
        String key = buildCacheKey(sessionId, messageId);
        try {
            // 设置初始内容，并设置过期时间
            stringRedisTemplate.opsForValue().set(key, initialContent, AiCacheExpireEnum.STREAM_CACHE_SECONDS.getValue(), TimeUnit.SECONDS);
            log.debug("缓存流式消息成功，sessionId: {}, messageId: {}", sessionId, messageId);
        } catch (Exception e) {
            log.error("缓存流式消息失败，sessionId: {}, messageId: {}", sessionId, messageId, e);
        }
    }

    /**
     * 追加流式消息内容
     * 向Redis中已缓存的流式消息追加内容片段，并重置过期时间
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @param chunk 追加的内容片段
     * @return 追加后的完整内容
     */
    @Override
    public String appendStreamContent(String sessionId, String messageId, String chunk) {
        String key = buildCacheKey(sessionId, messageId);
        try {
            // 追加内容到 Redis
            Integer length = stringRedisTemplate.opsForValue().append(key, chunk);
            
            // 重置过期时间（防止追加过程中过期）
            stringRedisTemplate.expire(key, AiCacheExpireEnum.STREAM_CACHE_SECONDS.getValue(), TimeUnit.SECONDS);
            
            log.debug("追加流式内容成功，sessionId: {}, messageId: {}, 当前长度: {}",
                     sessionId, messageId, length);
            
            // 获取完整内容
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("追加流式内容失败，sessionId: {}, messageId: {}", sessionId, messageId, e);
            return null;
        }
    }

    /**
     * 获取流式消息内容
     * 从Redis中读取指定消息的完整缓存内容
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @return 缓存的消息内容，不存在时返回null
     */
    @Override
    public String getStreamContent(String sessionId, String messageId) {
        String key = buildCacheKey(sessionId, messageId);
        try {
            String content = stringRedisTemplate.opsForValue().get(key);
            log.debug("获取流式内容成功，sessionId: {}, messageId: {}, 内容长度: {}", 
                     sessionId, messageId, content != null ? content.length() : 0);
            return content;
        } catch (Exception e) {
            log.error("获取流式内容失败，sessionId: {}, messageId: {}", sessionId, messageId, e);
            return null;
        }
    }

    /**
     * 删除流式消息缓存
     * 从Redis中移除指定消息的缓存内容
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     */
    @Override
    public void removeStreamCache(String sessionId, String messageId) {
        String key = buildCacheKey(sessionId, messageId);
        try {
            Boolean deleted = stringRedisTemplate.delete(key);
            log.debug("删除流式缓存成功，sessionId: {}, messageId: {}, 删除结果: {}", 
                     sessionId, messageId, deleted);
        } catch (Exception e) {
            log.error("删除流式缓存失败，sessionId: {}, messageId: {}", sessionId, messageId, e);
        }
    }

    /**
     * 构建缓存 key
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @return Redis key
     */
    private String buildCacheKey(String sessionId, String messageId) {
        return AiRedisKeyEnum.STREAM_CACHE_PREFIX.getCode() + sessionId + ":" + messageId;
    }

    /**
     * 追加思考链内容
     * 向Redis中已缓存的思考链内容追加片段，并重置过期时间
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @param chunk 追加的思考内容片段
     * @return 追加后的完整思考内容
     */
    @Override
    public String appendThinkingContent(String sessionId, String messageId, String chunk) {
        String key = buildThinkingCacheKey(sessionId, messageId);
        try {
            Integer length = stringRedisTemplate.opsForValue().append(key, chunk);
            stringRedisTemplate.expire(key, AiCacheExpireEnum.STREAM_CACHE_SECONDS.getValue(), TimeUnit.SECONDS);
            log.debug("追加思考内容成功，sessionId: {}, messageId: {}, 当前长度: {}",
                     sessionId, messageId, length);
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("追加思考内容失败，sessionId: {}, messageId: {}", sessionId, messageId, e);
            return null;
        }
    }

    /**
     * 获取思考链内容
     * 从Redis中读取指定消息的思考链缓存内容
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @return 缓存的思考内容，不存在时返回null
     */
    @Override
    public String getThinkingContent(String sessionId, String messageId) {
        String key = buildThinkingCacheKey(sessionId, messageId);
        try {
            String content = stringRedisTemplate.opsForValue().get(key);
            log.debug("获取思考内容成功，sessionId: {}, messageId: {}, 内容长度: {}",
                     sessionId, messageId, content != null ? content.length() : 0);
            return content;
        } catch (Exception e) {
            log.error("获取思考内容失败，sessionId: {}, messageId: {}", sessionId, messageId, e);
            return null;
        }
    }

    /**
     * 删除思考链缓存
     * 从Redis中移除指定消息的思考链缓存内容
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     */
    @Override
    public void removeThinkingCache(String sessionId, String messageId) {
        String key = buildThinkingCacheKey(sessionId, messageId);
        try {
            Boolean deleted = stringRedisTemplate.delete(key);
            log.debug("删除思考缓存成功，sessionId: {}, messageId: {}, 删除结果: {}",
                     sessionId, messageId, deleted);
        } catch (Exception e) {
            log.error("删除思考缓存失败，sessionId: {}, messageId: {}", sessionId, messageId, e);
        }
    }

    /**
     * 构建思考链缓存键
     * 使用思考链缓存前缀拼接会话ID和消息ID生成Redis键
     *
     * @param sessionId 会话ID
     * @param messageId 消息ID
     * @return 思考链缓存的Redis键
     */
    private String buildThinkingCacheKey(String sessionId, String messageId) {
        return AiRedisKeyEnum.STREAM_THINKING_CACHE_PREFIX.getCode() + sessionId + ":" + messageId;
    }
}
