package kr.lasel.apiskeleton.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  static ObjectMapper objectMapper = new ObjectMapper();

  // Object를 json String 형태로 변환
  public static String convertObjectToJsonString(Object object) {
    return convertObjectToJsonString(object, false);
  }

  // Object를 json String 형태로 이쁘게 변환
  public static String convertObjectToJsonString(Object object, boolean pretty) {

    if (pretty) {
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    String jsonString = "";

    try {
      jsonString = objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.warn("{}", e);
    }

    return jsonString;
  }

  public static String objectToString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.warn("Cache write fail : ", e);
    }
    return null;
  }
  public static <T> T stringToObject(String json, Class<T> valueType) {
    try {
      return objectMapper.readValue(json, valueType);
    } catch (JsonProcessingException e) {
      log.warn("Cache to Object Fail : ", e);
    }
    return (T) new Object();
  }

}
