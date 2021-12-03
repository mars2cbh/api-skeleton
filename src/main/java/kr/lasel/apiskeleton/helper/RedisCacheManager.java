package kr.lasel.apiskeleton.helper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisCacheManager {

    @Value("@{spring.redis.prefix}")
    private String prefix;
    private static final String CACHE_COUNT_SUFFIX = "_hit";

    final
    RedisProperties redisProperties;

    StatefulRedisConnection<String, String> connection;
    RedisReactiveCommands<String, String> reactiveCommands;

    boolean isAvailable = false;
    RedisClient redisClient = null;

    public RedisCacheManager(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @PostConstruct
    private void init() {

        String redisUri = String.format("redis://%s:%d/%d",
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getDatabase());

        redisClient = RedisClient.create(redisUri);
    }

    private void reConnect() {

        log.info("Connecting Redis.");

        try {
            connection = redisClient.connect();
            reactiveCommands = connection.reactive();
            log.info("Connect Success.");
            refresh();
        } catch (Exception e) {
            log.error("Redis Connect Error. ");
        }
    }

    public void refresh() {

        if (connection == null) {
            reConnect();
            return;
        }

        log.debug("Redis Status is {}", connection.isOpen());

        if (testCache()) {
            isAvailable = true;
            log.debug("#### Redis Health Status : [ OK ]");
        } else {
            isAvailable = false;
            log.debug("#### Redis Health Status : [ ERROR ]");
        }
    }

    public void setCacheData(String cacheKey, String value, int expireSecond) {
        try {
            setCache(cacheKey, expireSecond, value);
            setCache(generateCacheCountKey(cacheKey), expireSecond, "0");
        } catch (Exception e) {
            log.debug("", e);
        }
    }

    public String getCacheData(String key, int expireCount) {

        String result = null;

        try {
            if (existCache(key)) {
                long cacheCount = incrementCache(generateCacheCountKey(key));
                log.debug("[#Cache] get cache hit count  : {}", cacheCount);

                if (cacheCount >= expireCount) {
                    log.debug("[#Cache] Deleted Key : {}", key);
                    removeCache(key);
                } else {
                    result = getCache(key);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

    public long incrementCache(String key) {

        if (!connection.isOpen()) {
            return 0;
        }

        Long result = reactiveCommands.incr(key).block();
        if (result != null) {
            return result;
        } else {
            return 0;
        }
    }

    public long removeCache(String key) {
        if (!connection.isOpen()) {
            return 0;
        }

        Long result = reactiveCommands.del(key).block();

        if (result != null) {
            return result;
        } else {
            return 0;
        }
    }

    public boolean existCache(String key) {
        if (!connection.isOpen()) {
            return false;
        }

        Long exist = reactiveCommands.exists(key).block();

        if (exist != null && exist > 0) {
            return true;
        } else {
            return false;
        }
    }

    // result cache key gen
//    public String generateCacheKey(String string) {
//        String uuid = DigestUtils.md5DigestAsHex(string.getBytes(StandardCharsets.UTF_8)).toUpperCase();
//        return prefix + uuid;
//    }

    // hit count cache key gen
    public String generateCacheCountKey(String key) {
        return key + CACHE_COUNT_SUFFIX;
    }

    private boolean setCache(String key, String value) {

        String result = null;

        if (!connection.isOpen()) {
            return false;
        }

        try {
            result = reactiveCommands.set(key, value)
                    .block();
        } catch (Exception e) {
            log.debug("", e);
        }
        return "ok".equalsIgnoreCase(result);
    }

    private boolean setCache(String key, int expireSecond, String value) {
        String result = null;

        if (!connection.isOpen()) {
            return false;
        }

        try {
            result = reactiveCommands.setex(key, expireSecond, value).block();
        } catch (Exception e) {
            log.debug("", e);
        }

        if ("ok".equalsIgnoreCase(result)) {
            log.debug("[#Cache] set cache : {}", key);
            return true;
        } else {
            log.debug("[#Cache] Failed to set cache! : {}", key);
            return false;
        }
    }

    public String getCache(String key) {
        String result = null;

        if (!connection.isOpen()) {
            return null;
        }

        try {
            result = reactiveCommands.get(key).block();
        } catch (Exception e) {
            log.debug("", e);
        }

        return result;
    }

    private boolean testCache() {
        boolean result = false;

        try {
            setCache("ping", "pong");

            String str = getCache("ping");
            if (str != null && str.equals("pong")) {
                result = true;
            }

        } catch (Exception e) {
            log.warn("TestCache : ", e);
        }

        return result;

    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduleRefresh() throws Exception {
        log.debug("##### CacheHealthChecker(scheduler) : check redis health ...");
        refresh();
    }

    @PreDestroy
    private void destroy() {
        connection.close();
    }

}
