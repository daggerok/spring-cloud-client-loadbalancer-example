# Eureka Spring Cloud Client-Side LoadBalancer

```bash
# jdk11
jenv local 11.0
./mvnw

bash ./eureka-infra-server/target/*.jar &
npx wait-port 8761

bash ./rest-api-server/target/*.jar &
bash ./web-clients/step-1-manual/target/*.jar &
npx wait-port 8001

sleep 10s
http :8001/hello
http :8001/not/found/route

# killall -9 java
```

## resources

* [Eureka Reference](https://cloud.spring.io/spring-cloud-netflix/1.4.x/multi/multi__service_discovery_eureka_clients.html)
* [YouTube: Spring Tips: Spring Cloud Loadbalancer](https://www.youtube.com/watch?v=FDeOoKRKgkM)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.5.RELEASE/maven-plugin/)
* [Service Registration and Discovery](https://spring.io/guides/gs/service-registration-and-discovery/)
