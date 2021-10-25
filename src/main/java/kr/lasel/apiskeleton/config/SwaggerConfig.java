package kr.lasel.apiskeleton.config;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfiguration.Constants;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

@Configuration
public class SwaggerConfig {

  private final ApplicationContext context;

  public SwaggerConfig(ApplicationContext context) {
    this.context = context;
  }

  @Bean
  public Docket api_v1() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("API V1")
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("kr.lasel.apiskeleton"))
        .paths(PathSelectors.any())
        .build()
        .ignoredParameterTypes(ServerHttpRequest.class);
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("API Skeleton")
        .description("Skeleton API Docuements.")
        .version(context.getBean(BuildProperties.class).getVersion())
        .contact(new Contact("David Choi", null, null))
        .build();
  }

  @Bean
  UiConfiguration uiConfig() {
    return UiConfigurationBuilder.builder()
        .deepLinking(false)
        .displayOperationId(false)
        .defaultModelsExpandDepth(1)
        .defaultModelExpandDepth(1)
        .defaultModelRendering(ModelRendering.MODEL)
        .displayRequestDuration(false)
        .docExpansion(DocExpansion.LIST)
        .filter(false)
        .maxDisplayedTags(null)
        .operationsSorter(OperationsSorter.ALPHA)
        .showExtensions(false)
        .tagsSorter(TagsSorter.ALPHA)
        .supportedSubmitMethods(Constants.DEFAULT_SUBMIT_METHODS)
        .validatorUrl(null)
        .build();
  }
}
