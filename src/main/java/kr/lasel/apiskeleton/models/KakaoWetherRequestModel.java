package kr.lasel.apiskeleton.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoWetherRequestModel {

  @Builder.Default
  private String inputCoordSystem = "WCONGNAMUL";
  @Builder.Default
  private String outputCoordSystem = "WCONGNAMUL";
  @Builder.Default
  private String service = "map.daum.net";
  @Builder.Default
  private int version = 2;
  @Builder.Default
  private int x = 377380;
  @Builder.Default
  private int y = 1143330;

}
