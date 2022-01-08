package kr.lasel.apiskeleton.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoWetherResponseModel {

  private WeatherInfos weatherInfos;

  @JsonInclude(Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  @Data
  private static class WeatherInfos {
    private Info current;
    private Info forecast;
  }

  @JsonInclude(Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
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
