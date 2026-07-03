package com.blog.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.StandardCharsets;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(new FastJson2RedisSerializer());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(new FastJson2RedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    public static class FastJson2RedisSerializer implements RedisSerializer<Object> {

        @Override
        public byte[] serialize(Object obj) {
            if (obj == null) return new byte[0];
            if (obj instanceof String) return ((String) obj).getBytes(StandardCharsets.UTF_8);
            return JSON.toJSONString(obj, JSONWriter.Feature.WriteClassName).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public Object deserialize(byte[] bytes) {
            if (bytes == null || bytes.length == 0) return null;
            String str = new String(bytes, StandardCharsets.UTF_8);
            if (str.startsWith("{") || str.startsWith("[") || str.startsWith("\"")) {
                try {
                    return JSON.parse(str);
                } catch (Exception e) {
                    return str;
                }
            }
            return str;
        }
    }
}
