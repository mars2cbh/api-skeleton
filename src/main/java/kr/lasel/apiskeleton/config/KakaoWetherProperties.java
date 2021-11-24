package kr.lasel.apiskeleton.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kakaowether")
public class KakaoWetherProperties {
  private String scheme;
  private String host;
  private String path;
  private int connectTimeout;
  private int readTimeout;
  private int writeTimeout;
  private int poolCount;
}
