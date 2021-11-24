package kr.lasel.apiskeleton.services;

import java.util.concurrent.atomic.AtomicReference;
import kr.lasel.apiskeleton.context.KakaoWetherApiContext;
import kr.lasel.apiskeleton.models.KakaoWetherRequestModel;
import kr.lasel.apiskeleton.models.KakaoWetherResponseModel;
import kr.lasel.apiskeleton.models.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import springfox.documentation.service.Server;

@Service
public class MainService {

  private final KakaoWetherApiContext kakaoWetherApiContext;

  public MainService(KakaoWetherApiContext kakaoWetherApiContext) {
    this.kakaoWetherApiContext = kakaoWetherApiContext;
  }

  public String getHello() {
    return "Hello";
  }

  public Mono<KakaoWetherResponseModel> getWeather(KakaoWetherRequestModel requestModel) {
    return kakaoWetherApiContext.kakaoWetherResponseModelMono(requestModel);
  }

}
