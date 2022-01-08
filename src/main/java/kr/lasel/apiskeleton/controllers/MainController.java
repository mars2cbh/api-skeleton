package kr.lasel.apiskeleton.controllers;

import javax.validation.Valid;
import kr.lasel.apiskeleton.models.KakaoWetherRequestModel;
import kr.lasel.apiskeleton.models.KakaoWetherResponseModel;
import kr.lasel.apiskeleton.models.RequestModel;
import kr.lasel.apiskeleton.models.ResponseModel;
import kr.lasel.apiskeleton.services.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import springfox.documentation.service.Server;

@Slf4j
@RestController
public class MainController {

  private final MainService mainService;

  public MainController(MainService mainService) {
    this.mainService = mainService;
  }

  @GetMapping("/")
  public Mono<ResponseEntity<String>> index() {

    ResponseModel responseModel = new ResponseModel();

    HttpStatus status = HttpStatus.OK;

    responseModel.setStatus(status);
    responseModel.setMessage(status.getReasonPhrase());
    responseModel.setData(mainService.getHello());

    return Mono.just(responseModel.toResponse());

  }

  @GetMapping("/me")
  public Mono<ResponseEntity<String>> me(
      ServerHttpRequest httpRequest,
      @Valid RequestModel requestModel) {

    ResponseModel responseModel = new ResponseModel();
    responseModel.setData("Hello " + requestModel.getName()
        + ". Age is " + String.valueOf(requestModel.getAge()));

    return Mono.just(responseModel.toResponse());
  }

  @GetMapping("/weather")
  public Mono<ResponseEntity<String>> weather(
      ServerHttpRequest httpRequest) {

    ResponseModel responseModel = new ResponseModel();

    return mainService.getWeather(KakaoWetherRequestModel.builder().build())
        .map(kakaoWetherResponseModel -> {
          responseModel.setData(kakaoWetherResponseModel);
          return responseModel.toResponse();
        });
  }

}
