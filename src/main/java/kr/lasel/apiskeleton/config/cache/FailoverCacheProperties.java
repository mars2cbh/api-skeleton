package kr.lasel.apiskeleton.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cache.failover")
public class FailoverCacheProperties {
  private String host;
  private int port;
  private int database;
  private int timeout;
  private String prefix;
  private int expireSeconds;
  private int limitCount;
}
