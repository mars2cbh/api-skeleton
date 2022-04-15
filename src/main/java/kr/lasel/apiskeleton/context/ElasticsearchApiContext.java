package kr.lasel.apiskeleton.context;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.json.stream.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import kr.lasel.apiskeleton.config.ElasticsearchProperties;
import kr.lasel.apiskeleton.models.BookInfoModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class ElasticsearchApiContext {

  private final ElasticsearchProperties elasticsearchProperties;
  private ElasticsearchAsyncClient asyncClient;
  private ElasticsearchTransport transport;

  public ElasticsearchApiContext(ElasticsearchProperties elasticsearchProperties) {
    this.elasticsearchProperties = elasticsearchProperties;
  }

  @PostConstruct
  private void init() {

    RestClient restClient = RestClient.builder(
        elasticsearchProperties.getHosts()
            .stream()
            .map(HttpHost::create)
            .toArray(HttpHost[]::new)
        )
        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
            .setConnectTimeout(elasticsearchProperties.getConnectTimeout())
            .setConnectionRequestTimeout(elasticsearchProperties.getConnectRequestTimeout())
            .setSocketTimeout(elasticsearchProperties.getSocketTimeout())
        )
        .setCompressionEnabled(true)
        .build();

    transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
    asyncClient = new ElasticsearchAsyncClient(transport);

  }

  private String queryToString(Query query) {

    if (query == null)
      return null;

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      JsonGenerator generator = transport.jsonpMapper().jsonProvider().createGenerator(byteArrayOutputStream);
      query.serialize(generator, transport.jsonpMapper());
      generator.close();
      return byteArrayOutputStream.toString();
    } catch (IOException e) {
      log.debug("", e);
    }
    return null;
  }


  public Mono<SearchResponse<BookInfoModel>> getNodes() {

    SearchRequest searchRequest = SearchRequest.of(s -> s
        .index("mars")
        .query(q -> q
            .range(r -> r
                .field("title")
                .gte(JsonData.of(3))
            )
        ));

    String q = queryToString(searchRequest.query());
    log.debug("QUERY : {}", q);

    return Mono.<SearchResponse<BookInfoModel>>create(
        sink -> {
          asyncClient.search(searchRequest, BookInfoModel.class)
              .whenComplete((mapSearchResponse, throwable) -> {
                if (throwable != null) {
                  sink.error(throwable);
                } else {
                  sink.success(mapSearchResponse);
                }
              });
        }).publishOn(Schedulers.boundedElastic());
  }

}
