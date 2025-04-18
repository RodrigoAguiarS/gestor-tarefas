package br.com.rodrigo.gestortarefas.api.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(com.github.benmanes.caffeine.cache.Caffeine<Object, Object> caffeineCacheBuilder) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("tarefas", "usuarios", "produtos", "categorias", "perfis");
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

    @Bean
    public com.github.benmanes.caffeine.cache.Caffeine<Object, Object> caffeineCacheBuilder() {
        return com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(10, java.util.concurrent.TimeUnit.MINUTES)
                .recordStats();
    }
}