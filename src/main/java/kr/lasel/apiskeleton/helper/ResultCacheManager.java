package kr.lasel.apiskeleton.helper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import kr.lasel.apiskeleton.config.cache.ResultCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ResultCacheManager {

  private static final String EMPTY_STRING = "";
  private static final String CACHE_COUNT_SUFFIX = "_hit";

  final
  ResultCacheProperties resultCacheProperties;

  StatefulRedisConnection<String, String> connection;
  RedisReactiveCommands<String, String> reactiveCommands;

  boolean isAvailable = false;
  RedisClient redisClient = null;

  public ResultCacheManager(ResultCacheProperties resultCacheProperties) {
    this.resultCacheProperties = resultCacheProperties;
  }

  @PostConstruct
  private void init() {

    String redisUri = String.format("redis://%s:%d/%d",
        resultCacheProperties.getHost(),
        resultCacheProperties.getPort(),
        resultCacheProperties.getDatabase());

    redisClient = RedisClient.create(redisUri);

    refresh();
  }

  private void reConnect() {

    log.info("Connecting Redis.");

    try {
      connection = redisClient.connect();
      reactiveCommands = connection.reactive();
      log.info("Redis Connect Success.");
      refresh();
    } catch (Exception e) {
      log.error("Redis Connect Error : {}", e.getLocalizedMessage());
    }
  }

  public void refresh() {

    if (connection == null) {
      reConnect();
      return;
    }

//    log.debug("Redis Status is {}", connection.isOpen());

    testCache()
        .flatMap(aBoolean -> {
          if (aBoolean) {
            isAvailable = true;
            log.debug("#### Redis Health Status : [ OK ]");
          } else {
            isAvailable = false;
            log.debug("#### Redis Health Status : [ ERROR ]");
          }
          return Mono.just(isAvailable);
        })
        .subscribe();
  }

  public void setCacheData(String key, String value) {

    String cacheKey = generateCacheKey(key);

    try {

      setCache(cacheKey, resultCacheProperties.getExpireSeconds(), value)
          .subscribe(successValue -> {
            setCache(generateCacheCountKey(cacheKey), resultCacheProperties.getExpireSeconds(), "0").subscribe();
          }, error -> {
            log.debug("set key {} error : {}", cacheKey, error.getLocalizedMessage());
          });

    } catch (Exception e) {
      log.debug("Set Cache Error : ", e);
    }
  }

  public Mono<String> getCacheData(String key) {

    String cacheKey = generateCacheKey(key);

    return existCache(cacheKey)
        .flatMap(aBoolean -> {
          if (aBoolean) {
            return incrementCache(generateCacheCountKey(cacheKey))
                .flatMap(cacheCount -> {
                  log.debug("[#Cache] get cache hit count : {}", cacheCount);
                  if (cacheCount > resultCacheProperties.getExpireCount()) {
                    log.debug("[#Cache] remove cache : {}", cacheKey);
                    return removeCache(cacheKey)
                        .flatMap(aLong -> Mono.just(EMPTY_STRING));
                  } else {
                    return getCache(cacheKey);
                  }
                });
          } else {
            return Mono.just(EMPTY_STRING);
          }
        });
  }

  private Mono<Long> incrementCache(String key) {
    return reactiveCommands.incr(key)
        .flatMap(Mono::just);
  }

  private Mono<Long> removeCache(String key) {
    return reactiveCommands.del(key)
        .flatMap(Mono::just);

  }

  private Mono<Boolean> existCache(String key) {

    if (connection == null || !connection.isOpen()) {
      return Mono.just(false);
    }

    return reactiveCommands.exists(key)
        .doOnError(ex -> {
          log.debug("[#Cache] Failed to exist cache! {} : {}", key, ex);
        })
        .map(aLong -> {
          if (aLong > 0) {
            log.debug("[#Cache] exist cache : {}.", key);
          }
          return aLong > 0;
        });
  }

  // result cache key gen
  private String generateCacheKey(String string) {
      String uuid = DigestUtils.md5DigestAsHex(string.getBytes(StandardCharsets.UTF_8)).toUpperCase();
      return resultCacheProperties.getPrefix() + uuid;
  }

  // hit count cache key gen
  private String generateCacheCountKey(String key) {
    return key + CACHE_COUNT_SUFFIX;
  }

  private Mono<Boolean> setCache(String key, String value) {

    if (connection == null || !connection.isOpen()) {
      return Mono.just(false);
    }

    return reactiveCommands.set(key, value)
        .flatMap(s -> {
          if (s.equalsIgnoreCase("ok")) {
            return Mono.just(true);
          } else {
            return Mono.just(false);
          }
        });

  }

  private Mono<Boolean> setCache(String key, int expireSecond, String value) {

    if (connection == null || !connection.isOpen()) {
      return Mono.just(false);
    }

    return reactiveCommands.setex(key, expireSecond, value)
        .doOnSuccess(e -> {
          log.debug("[#Cache] set cache : {}", key);
        })
        .doOnError(e -> {
          log.warn("[#Cache] Failed to set cache! : {} : {}", key, e.getLocalizedMessage());
        })
        .flatMap(s -> {
          if (s.equalsIgnoreCase("ok")) {
            return Mono.just(true);
          } else {
            return Mono.just(false);
          }
        });
  }

  private Mono<String> getCache(String key) {
    return reactiveCommands.get(key)
        .flatMap(Mono::just);
  }

  private Mono<Boolean> testCache() {

    return setCache("ping", "pong")
        .doOnError(throwable -> {
          log.warn("TestCache : ", throwable);
        })
        .doOnNext(aBoolean -> {
          getCache("ping").flatMap(s -> {
            if (s.equalsIgnoreCase("pong")) {
              return Mono.just(true);
            } else {
              return Mono.just(false);
            }
          });
        });
  }

  @Scheduled(cron = "0 * * * * *")
  public void scheduleRefresh() throws Exception {
    log.debug("##### CacheHealthChecker(scheduler) : check redis health ...");
    refresh();
  }

  @PreDestroy
  private void destroy() {
    if (connection != null && connection.isOpen()) {
      connection.close();
    }
  }

}
