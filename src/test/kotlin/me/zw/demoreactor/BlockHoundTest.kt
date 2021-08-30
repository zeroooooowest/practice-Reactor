package me.zw.demoreactor

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

class BlockHoundTest {

    @Test
    fun threadSleepIsABlockingCall() {

        // 블록하운드에 의해 실패
        Mono.delay(Duration.ofSeconds(1))
            .flatMap { tick ->
                try {
                    Thread.sleep(10)
                    Mono.just(true)
                } catch (e: InterruptedException) {
                    Mono.error(e)
                }
            }
            .`as` { StepVerifier.create(it) }
            .verifyComplete()
    }
}