package kr.lasel.apiskeleton.models;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestModel {

  @NotBlank(message = "`name` cannot be null")
  private String name;
  @NotNull(message = "`age` cannot be null")
  @Min(value = 0, message = "`age` should not be less than 0")
  @Max(value = 150, message = "`age` should not be greater than 150")
  private Integer age;

}
