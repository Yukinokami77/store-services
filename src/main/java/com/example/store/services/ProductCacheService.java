package com.example.store.services;

import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductCacheService {

    private final RedisClient redisClient;

    public ProductCacheService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public String getCachedProduct(Long productId) {
        Response response = redisClient.get("product:" + productId);
        return response != null ? response.toString() : null;
    }

    public void cacheProduct(Long productId, String productData) {
        redisClient.setex("product:" + productId, "3600", productData); // Кэш на 1 час
    }
}
