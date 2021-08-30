package me.zw.demoreactor

import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import reactor.core.scheduler.Schedulers
import java.util.*
import java.util.concurrent.Executors


// JVM 스택 트레이스는 동일한 스레드 내에서만 이어져서, 스레드 경계를 넘어서지 못한다.
fun main() {
//    byExecutor()
//    byReactor()
    byReactorForDebug()
}


fun byExecutor() {
    val executor = Executors.newSingleThreadScheduledExecutor()

    val source =
        if (Random().nextBoolean())
            IntRange(1, 11).toList()
        else
            listOf(1, 2, 3, 4)

    try {
        executor.submit { source.get(5) }.get()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        executor.shutdown()
    }
}

fun byReactor() {

    // 리액터는 스택 트레이스를 통해 가능한 가장 먼 곳까지 따라가지만 다른 스레드의 내용까지 쫓아가지는 못한다.
    val source =
        if (Random().nextBoolean())
            Flux.range(1, 10).elementAt(5)
        else
            Flux.just(1, 2, 3, 4).elementAt(5)

    source.subscribeOn(Schedulers.parallel())
        .block()
}


fun byReactorForDebug() {

    // 리액터 백트레이싱 활성화
    // 리액터가 처리 흐름 조립 시점에서 호출부의 세부 정보를 수집하고, 구독해서 실행되는 시점에 세부정보를 넘겨준다.
    Hooks.onOperatorDebug()

    val source =
        if (Random().nextBoolean())
            Flux.range(1, 10).elementAt(5)
        else
            Flux.just(1, 2, 3, 4).elementAt(5)

    source.subscribeOn(Schedulers.parallel())
        .block()
}
