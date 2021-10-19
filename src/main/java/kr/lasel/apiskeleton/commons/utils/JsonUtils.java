package kr.lasel.apiskeleton.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  // Object를 json String 형태로 변환
  public static String convertObjectToJsonString(Object object) {
    return convertObjectToJsonString(object, false);
  }

  // Object를 json String 형태로 이쁘게 변환
  public static String convertObjectToJsonString(Object object, boolean pretty) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    if (pretty) {
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    String jsonString = "";

    try {
      jsonString = mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.warn("{}", e);
    }

    return jsonString;
  }
}
