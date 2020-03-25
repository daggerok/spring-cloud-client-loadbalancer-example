package daggerok.eurekaserverinfrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerInfrastructureApplication {

  public static void main(String[] args) {
    SpringApplication.run(EurekaServerInfrastructureApplication.class, args);
  }
}
