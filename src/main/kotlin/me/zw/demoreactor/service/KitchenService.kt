package me.zw.demoreactor.service

import me.zw.demoreactor.model.Dish
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

@Service
class KitchenService {

    private val menu = listOf(
        Dish("Sesame chicken"),
        Dish("Lo mein noodles, plain"),
        Dish("Sweet & sour beef")
    )

    private val picker = Random()


    fun dishes(): Flux<Dish> {
        return Flux.generate<Dish> { sink ->
            sink.next(randomDish())
        }.delayElements(Duration.ofMillis(250))
    }


    fun randomDish(): Dish {
        return menu[picker.nextInt(menu.size)]
    }

}
