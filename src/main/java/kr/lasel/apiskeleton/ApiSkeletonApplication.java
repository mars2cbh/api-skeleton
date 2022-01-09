package kr.lasel.apiskeleton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ApiSkeletonApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiSkeletonApplication.class, args);
  }

}
