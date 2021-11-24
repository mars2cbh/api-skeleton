package kr.lasel.apiskeleton.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class KakaoWetherResponseModel {

  private WeatherInfos weatherInfos;

  @Data
  private static class WeatherInfos {
    private Info current;
    private Info forecast;
  }

  @Data
  private static class Info {
    private String type;
    private String rcode;
    private String iconId;
    private String temperature;
    private String desc;
    private String humidity;
    private String rainfall;
    private String snowfall;
  }


}
