package me.zw.demoreactor

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration


class CreateTests {

    @Test
    fun createFlux_just() {
        val fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry")

        StepVerifier.create(fruitFlux)
            .expectNext("Apple")
            .expectNext("Orange")
            .expectNext("Grape")
            .expectNext("Banana")
            .expectNext("Strawberry")
            .verifyComplete()
    }

    @Test
    fun createFlux_fromArray() {
        val fruitFlux = Flux.fromArray(arrayOf("Apple", "Orange", "Grape", "Banana", "Strawberry"))

        StepVerifier.create(fruitFlux)
            .expectNext("Apple")
            .expectNext("Orange")
            .expectNext("Grape")
            .expectNext("Banana")
            .expectNext("Strawberry")
            .verifyComplete()
    }

    @Test
    fun createFlux_range() {
        val intervalFlux = Flux.range(1, 5)

        StepVerifier.create(intervalFlux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .expectNext(4)
            .expectNext(5)
            .verifyComplete()
    }

    @Test
    fun createFlux_interval() {
        val intervalFlux = Flux.interval(Duration.ofSeconds(1))
            .take(5)

        StepVerifier.create(intervalFlux)
            .expectNext(0L)
            .expectNext(1L)
            .expectNext(2L)
            .expectNext(3L)
            .expectNext(4L)
            .verifyComplete()
    }

}