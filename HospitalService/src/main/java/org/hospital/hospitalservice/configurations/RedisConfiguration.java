package org.hospital.hospitalservice.configurations;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("doctorByChatID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("doctorByID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("pageWithDoctors",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("doctorsByPatientID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("patientByID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("patientByChatID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("patientByID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("userByToken",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("userByChatID",
                        defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
    }
}
