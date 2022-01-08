package kr.lasel.apiskeleton.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import kr.lasel.apiskeleton.config.KakaoWetherProperties;
import kr.lasel.apiskeleton.helper.ResultCacheManager;
import kr.lasel.apiskeleton.models.KakaoWetherRequestModel;
import kr.lasel.apiskeleton.models.KakaoWetherResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Slf4j
@Component
public class KakaoWetherApiContext {

  private final KakaoWetherProperties kakaoWetherProperties;
  private final ObjectMapper objectMapper;
  private final ResultCacheManager resultCacheManager;

  private WebClient webClient;

  public KakaoWetherApiContext(
      KakaoWetherProperties kakaoWetherProperties,
      ObjectMapper objectMapper, ResultCacheManager resultCacheManager) {
    this.kakaoWetherProperties = kakaoWetherProperties;
    this.objectMapper = objectMapper;
    this.resultCacheManager = resultCacheManager;
  }

  @PostConstruct
  private void init() {
    ConnectionProvider connectionProvider = ConnectionProvider.builder("KakaoWeatherProvider")
        .maxConnections(kakaoWetherProperties.getPoolCount())
        .lifo()
        .metrics(true)
        .build();

    webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(
            HttpClient.create(connectionProvider)
                .headers(entries -> entries
                    .add("User-Agent", "ApiSkelton"))
                .compress(true)
                .followRedirect(true)
                .keepAlive(true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                    kakaoWetherProperties.getConnectTimeout())
                .doOnConnected(connection -> connection
                    .addHandlerLast(new ReadTimeoutHandler(kakaoWetherProperties.getReadTimeout(), TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(kakaoWetherProperties.getWriteTimeout(), TimeUnit.MILLISECONDS))
                )
        ))
//        .filter(logResponse())
        .build();
  }

  private static ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      HttpStatus status = clientResponse.statusCode();
      log.debug("Returned status code {} ({})", status.value(), status.getReasonPhrase());
      return logBody(clientResponse);
    });
  }

  private static Mono<ClientResponse> logBody(ClientResponse response) {
    return response.bodyToMono(String.class)
        .flatMap(body -> {
          log.debug("Body is {}", body);
          return Mono.just(response);
        });
  }

  public Mono<KakaoWetherResponseModel> kakaoWetherResponseModelMono(KakaoWetherRequestModel requestModel) {

    String key = requestModel.toString();

    return resultCacheManager.getCacheData(key)
        .flatMap(s -> {
          if (StringUtils.hasText(s)) {
            return Mono.just(stringToObject(s, KakaoWetherResponseModel.class));
          } else {
            Map<String, String> map = objectMapper.convertValue(requestModel, new TypeReference<Map<String,String>>() {});
            LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
            map.forEach(linkedMultiValueMap::add);

            return webClient.get()
                .uri( uriBuilder -> uriBuilder
                    .scheme(kakaoWetherProperties.getScheme())
                    .host(kakaoWetherProperties.getHost())
                    .path(kakaoWetherProperties.getPath())
                    .queryParams(linkedMultiValueMap)
                    .build()
                )
                .retrieve()
                .bodyToMono(KakaoWetherResponseModel.class)
                .doOnNext(k -> {
                  resultCacheManager.setCacheData(key, this.objectToString(k));
                })
                .retry(3);
          }
        });
  }

  private String objectToString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.warn("Cache write fail : ", e);
    }
    return null;
  }
  private <T> T stringToObject(String json, Class<T> valueType) {
    try {
      return objectMapper.readValue(json, valueType);
    } catch (JsonProcessingException e) {
      log.warn("Cache to Object Fail : ", e);
    }
    return (T) new Object();
  }

}
