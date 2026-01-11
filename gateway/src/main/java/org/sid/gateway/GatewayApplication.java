//package org.sid.gateway;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class GatewayApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(GatewayApplication.class, args);
//    }
//     //@Bean
//    public RouteLocator routes(RouteLocatorBuilder builder){
//        return builder.routes()
//                .route(r->r.path("/users/**").uri("lb://User"))
//                .route(r->r.path("/books/**").uri("lb://book"))
//                .build();
//    }
//    @Bean
//    public DiscoveryClientRouteDefinitionLocator dynamicRoutes(ReactiveDiscoveryClient rdc,
//                                                               DiscoveryLocatorProperties dlp){
//        return new DiscoveryClientRouteDefinitionLocator(rdc,dlp);
//    }
//
//}

package org.sid.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/users/**")
                        .uri("lb://USER-SERVICE"))
                .route("book-service", r -> r.path("/books/**")
                        .uri("lb://BOOK-SERVICE"))
                .route("emprunt-service", r -> r.path("/emprunts/**")
                        .uri("lb://EMPRUNT-SERVICE"))
                .route("notification-service", r -> r.path("/notifications/**")
                        .uri("lb://NOTIFICATION-SERVICE"))
                .build();
    }
}