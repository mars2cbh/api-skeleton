package kr.lasel.apiskeleton.config;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
  private List<String> hosts;
  private int connectTimeout;
  private int connectRequestTimeout;
  private int socketTimeout;
}
