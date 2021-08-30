package me.zw.demoreactor

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

class CombinationTests {

    @Test
    fun mergeFluxes() {
        val characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
            .delayElements(Duration.ofMillis(500))

        val foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
            .delaySubscription(Duration.ofMillis(250))
            .delayElements(Duration.ofMillis(500))

        val mergedFlux = characterFlux.mergeWith(foodFlux)

        StepVerifier.create(mergedFlux)
            .expectNext("Garfield")
            .expectNext("Lasagna")
            .expectNext("Kojak")
            .expectNext("Lollipops")
            .expectNext("Barbossa")
            .expectNext("Apples")
            .verifyComplete()
    }

    @Test
    fun zipFluxes() {
        val characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
        val foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")

        val zippedFlux = Flux.zip(characterFlux, foodFlux)

        StepVerifier.create(zippedFlux)
            .expectNextMatches { it.t1 == "Garfield" && it.t2 == "Lasagna" }
            .expectNextMatches { it.t1 == "Kojak" && it.t2 == "Lollipops" }
            .expectNextMatches { it.t1 == "Barbossa" && it.t2 == "Apples" }
            .verifyComplete()
    }

    @Test
    fun zipFluxesToObject() {
        val characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
        val foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")

        val zippedFlux = Flux.zip(characterFlux, foodFlux) { c, f -> "$c eats $f" }

        StepVerifier.create(zippedFlux)
            .expectNext("Garfield eats Lasagna")
            .expectNext("Kojak eats Lollipops")
            .expectNext("Barbossa eats Apples")
            .verifyComplete()
    }

    @Test
    fun firstFlux() {
        val slowFlux = Flux.just("tortoise", "snail", "sloth")
            .delaySubscription(Duration.ofMillis(100))
        val fastFlux = Flux.just("hare", "cheetah", "squirrel")

        val firstFlux = Flux.firstWithSignal(slowFlux, fastFlux)

        StepVerifier.create(firstFlux)
            .expectNext("hare")
            .expectNext("cheetah")
            .expectNext("squirrel")
            .verifyComplete()
    }
}