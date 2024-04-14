package org.telegramchat.chat.configurations;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.*;
import static org.springframework.data.redis.cache.RedisCacheManager.*;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.*;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .disableCachingNullValues()
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }


    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("pageWithChatStates",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("chatStateByChatID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("pageWithAuthentication",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("authenticationByChatID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
    }
}
