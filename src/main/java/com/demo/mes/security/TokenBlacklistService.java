package com.demo.mes.security;

import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT Token 内存黑名单（演示系统使用，生产环境建议替换为 Redis）。
 * 在退出登录时将 token 加入黑名单，过滤器每次请求检查。
 */
@Component
public class TokenBlacklistService {

    /** key: token, value: 过期时间戳（毫秒） */
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * 将 token 加入黑名单
     * @param token 原始 token（不带 Bearer 前缀）
     * @param expireAt 过期时间戳（毫秒）
     */
    public void invalidate(String token, long expireAt) {
        blacklist.put(token, expireAt);
    }

    /**
     * 检查 token 是否已被注销
     */
    public boolean isBlacklisted(String token) {
        cleanupExpired();
        return blacklist.containsKey(token);
    }

    /**
     * 清理过期 token，防止内存泄漏
     */
    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> it = blacklist.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            if (entry.getValue() < now) {
                it.remove();
            }
        }
    }
}
