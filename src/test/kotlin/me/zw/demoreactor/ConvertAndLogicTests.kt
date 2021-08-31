package me.zw.demoreactor

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.time.Duration

class ConvertAndLogicTests {

    @Test
    fun skipAFew() {
        val skipFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
            .skip(3)

        StepVerifier.create(skipFlux)
            .expectNext("ninety nine", "one hundred")
            .verifyComplete()
    }

    @Test
    fun skipAFewSeconds() {
        val skipFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
            .delayElements(Duration.ofSeconds(1))
            .skip(Duration.ofSeconds(4))

        StepVerifier.create(skipFlux)
            .expectNext("ninety nine", "one hundred")
            .verifyComplete()
    }

    @Test
    fun delaySubscriptions() {
        val delaySubscriptionFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
            .delaySubscription(Duration.ofSeconds(1))
            .skip(Duration.ofMillis(999))

        StepVerifier.create(delaySubscriptionFlux)
            .expectNext("one")
            .expectNext("two")
            .expectNext("skip a few")
            .expectNext("ninety nine")
            .expectNext("one hundred")
            .verifyComplete()
    }

    @Test
    fun take() {
        val nationalParkFlux = Flux.just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
            .take(3)

        StepVerifier.create(nationalParkFlux)
            .expectNext("Yellowstone", "Yosemite", "Grand Canyon")
            .verifyComplete()
    }

    @Test
    fun takeTime() {
        val nationalParkFlux = Flux.just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
            .delayElements(Duration.ofSeconds(1))
            .take(Duration.ofMillis(3500))

        StepVerifier.create(nationalParkFlux)
            .expectNext("Yellowstone", "Yosemite", "Grand Canyon")
            .verifyComplete()
    }

    @Test
    fun filter() {
        val nationalParkFlux = Flux.just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
            .filter { !it.contains(" ") }

        StepVerifier.create(nationalParkFlux)
            .expectNext("Yellowstone", "Yosemite", "Zion")
            .verifyComplete()
    }

    @Test
    fun distinct() {
        val animalFlux = Flux.just("dog", "cat", "bird", "dog", "bird", "anteater")
            .distinct()

        StepVerifier.create(animalFlux)
            .expectNext("dog", "cat", "bird", "anteater")
            .verifyComplete()
    }

    @Test
    fun map() {
        val playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
            .map {
                val split = it.split(" ")
                Player(split[0], split[1])
            }

        StepVerifier.create(playerFlux)
            .expectNext(Player("Michael", "Jordan"))
            .expectNext(Player("Scottie", "Pippen"))
            .expectNext(Player("Steve", "Kerr"))
            .verifyComplete()
    }

    @Test
    fun flatMap() {
        val playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
            .flatMap {
                Mono.just(it)
                    .map {
                        val split = it.split(" ")
                        Player(split[0], split[1])
                    }
                    .subscribeOn(Schedulers.parallel())
            }

        val playerList = listOf(
            Player("Michael", "Jordan"),
            Player("Scottie", "Pippen"),
            Player("Steve", "Kerr")
        )

        // 병행 스레드에 작업을 분할 했으므로 순서 보장이 안된다. 단지 존재하는지, 3개인지만 검사
        StepVerifier.create(playerFlux)
            .expectNextMatches { playerList.contains(it) }
            .expectNextMatches { playerList.contains(it) }
            .expectNextMatches { playerList.contains(it) }
            .verifyComplete()
    }


    @Test
    fun flatMap2() {
        val fruitList =
            listOf("apple", "banana", "melon", "mango", "grape", "strawberry", "eggplant", "watermelon", "kiwi")
        val fruitListUpper = fruitList.map { it.toUpperCase() }

        val fruitFlux =
            Flux.fromIterable(fruitList)
                .window(1)
                .flatMap {
                    it.map { str: String ->
                        Thread.sleep(1000) // 1초짜리 요청

                        str.toUpperCase()
                    }.subscribeOn(Schedulers.parallel()) // map 작업을 비동기적으로
                }

        // 순서 보장X
        StepVerifier.create(fruitFlux)
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .expectNextMatches { fruitListUpper.contains(it) }
            .verifyComplete()
//            .expectNext("APPLE")
//            .expectNext("BANANA")
//            .expectNext("MELON")
//            .expectNext("MANGO")
//            .expectNext("GRAPE")
//            .expectNext("STRAWBERRY")
//            .expectNext("EGGPLANT")
//            .expectNext("WATERMELON")
//            .expectNext("KIWI")
//            .verifyComplete()
    }

    @Test
    fun flatMapSequential() {
        val fruitList =
            listOf("apple", "banana", "melon", "mango", "grape", "strawberry", "eggplant", "watermelon", "kiwi")
        val fruitListUpper = fruitList.map { it.toUpperCase() }

        val fruitFlux =
            Flux.fromIterable(fruitList)
                .window(1)
                .flatMapSequential {
                    it.map { str: String ->
                        Thread.sleep(1000)

                        str.toUpperCase()
                    }.subscribeOn(Schedulers.parallel())
                }

        // 순서 보장
        StepVerifier.create(fruitFlux)
            .expectNext("APPLE")
            .expectNext("BANANA")
            .expectNext("MELON")
            .expectNext("MANGO")
            .expectNext("GRAPE")
            .expectNext("STRAWBERRY")
            .expectNext("EGGPLANT")
            .expectNext("WATERMELON")
            .expectNext("KIWI")
            .verifyComplete()
    }

    @Test
    fun buffer() {
        val fruitFlux = Flux.just("apple", "orange", "banana", "kiwi", "strawberry")

        val bufferedFlux = fruitFlux.buffer(3)

        StepVerifier.create(bufferedFlux)
            .expectNext(listOf("apple", "orange", "banana"))
            .expectNext(listOf("kiwi", "strawberry"))
            .verifyComplete()
    }

    @Test
    fun bufferFlatMap() {
        Flux.just("apple", "orange", "banana", "kiwi", "strawberry")
            .buffer(3)
            .flatMap { list ->
                Flux.fromIterable(list)
                    .map { it.toUpperCase() }
                    .subscribeOn(Schedulers.parallel())
                    .log()
            }.subscribe()
    }

    @Test
    fun collectList() {
        val fruitFlux = Flux.just("apple", "orange", "banana", "kiwi", "strawberry")

        val fruitListMono: Mono<List<String>> = fruitFlux.collectList()

        StepVerifier.create(fruitListMono)
            .expectNext(listOf("apple", "orange", "banana", "kiwi", "strawberry"))
            .verifyComplete()
    }

    @Test
    fun collectMap() {
        val animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo")

        val animalMapMono: Mono<Map<Char, String>> = animalFlux.collectMap { it[0] }

        StepVerifier.create(animalMapMono)
            .expectNextMatches { map ->
                return@expectNextMatches map.size == 3
                        && map['a'] == "aardvark"
                        && map['e'] == "eagle"
                        && map['k'] == "kangaroo"
            }
            .verifyComplete()
    }

    @Test
    fun all() {
        val animalFlux = Flux.just(
            "aardvark", "elephant", "koala", "eagle", "kangaroo"
        )

        val hasAMono = animalFlux.all { it.contains("a") }
        StepVerifier.create(hasAMono)
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun any() {
        val animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo")

        val hasTMono = animalFlux.any { it.contains("t") }

        StepVerifier.create(hasTMono)
            .expectNext(true)
            .verifyComplete()
    }
}

data class Player(
    val lastName: String,
    val firstName: String,
)