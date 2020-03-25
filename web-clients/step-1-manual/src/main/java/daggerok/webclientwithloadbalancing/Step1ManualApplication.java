package daggerok.webclientwithloadbalancing;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Log4j2
@Configuration
class ReactiveLoadBalancerFactoryConfig {

  @Bean
  WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  ReactiveLoadBalancer<ServiceInstance> serviceInstance(ReactiveLoadBalancer.Factory<ServiceInstance> factory) {
    return factory.getInstance("rest-api-server");
  }

  @Bean
  Function<String, Flux<ClientResponse>> serviceFactory(WebClient webClient,
                                                        ReactiveLoadBalancer<ServiceInstance> serviceInstance) {
    var chosen = serviceInstance.choose();
    var flux = Flux.from(chosen);
    return path -> flux.map(this::toBaseUrl)
                       .map(baseUrl -> baseUrl + path)
                       .flatMap(url -> webClient.get()
                                                .uri(url)
                                                .exchange());
    // .flatMap(clientResponse -> clientResponse.bodyToMono(Map.class));
  }

  private String toBaseUrl(Response<ServiceInstance> instanceFactory) {
    var uri = Objects.requireNonNull(instanceFactory.getServer(), "Server cannot be discovered yet...")
                     .getUri();
    return String.format("%s://%s", uri.getScheme(), uri.getAuthority());
  }
}

@Log4j2
@RestController
@RequiredArgsConstructor
class RestResource {

  final Function<String, Flux<ClientResponse>> serviceFactory;

  @GetMapping("/hello")
  Object hello() {
    return serviceFactory.apply("/api/hello")
                         .flatMap(response -> response.bodyToMono(Map.class))
                         .doOnNext(map -> log.info("hello: {}", map));
  }

  @RequestMapping("/**")
  Object generic(ServerWebExchange exchange) {
    return serviceFactory.apply(exchange.getRequest().getPath().toString())
                         .flatMap(response -> response.bodyToMono(Map.class))
                         .doOnNext(map -> log.info("generic: {}", map));
  }
}

@EnableEurekaClient
@SpringBootApplication
public class Step1ManualApplication {

  public static void main(String[] args) {
    SpringApplication.run(Step1ManualApplication.class, args);
  }
}
