package com.example.rtb.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CampaignCacheService {

    private final StringRedisTemplate redisTemplate;

    public CampaignCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String buildKey(String country, String device, String content) {
        return "campaign:" + country + ":" + device + ":" + content;
    }

    public Optional<String> getCampaignId(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void cacheCampaignId(String key, Long campaignId) {
        redisTemplate.opsForValue().set(key, String.valueOf(campaignId), 5, TimeUnit.MINUTES);
    }

    public void cacheNoMatch(String key) {
        redisTemplate.opsForValue().set(key, "NONE", 1, TimeUnit.MINUTES);
    }
}
