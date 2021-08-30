package me.zw.demoreactor

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

// RouterFunction 활용 예
@Configuration
class RouterFunctionConfig {

    @Bean
    fun helloRouterFunction() =
        route(GET("/hello")) { req ->
            ok().body(Mono.just("Hello World"), String::class.java)
        }
            .andRoute(GET("/bye")) { req ->
                ok().body(Mono.just("See ya!"), String::class.java)
            }

}