package daggerok.restapiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@EnableEurekaClient
@SpringBootApplication
public class RestApiServerApplication {

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route().nest(path("/api"),
                        builder -> builder.GET("/hello", this::helloHandler))
                  .build()
                  .andRoute(path("/**"), this::fallbackHandler);
  }

  @NonNull
  Mono<ServerResponse> helloHandler(ServerRequest serverRequest) {
    return ServerResponse.ok().body(Mono.just(Map.of("hello", "world")), Map.class);
  }

  @NonNull
  Mono<ServerResponse> fallbackHandler(ServerRequest serverRequest) {
    var uri = serverRequest.uri();
    var baseUrl = String.format("http://%s://%s", uri.getScheme(), uri.getAuthority());
    var api = Mono.just(Map.of("GET", baseUrl + "/api/hello",
                               "_self", String.format("%s %s", serverRequest.methodName(), uri)));
    return ServerResponse.ok().body(api, Map.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(RestApiServerApplication.class, args);
  }
}
