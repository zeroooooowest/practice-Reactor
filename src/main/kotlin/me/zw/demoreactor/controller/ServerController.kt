package me.zw.demoreactor.controller

import me.zw.demoreactor.model.Dish
import me.zw.demoreactor.service.KitchenService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ServerController(
    private val kitchen: KitchenService,
) {

    @GetMapping(value = ["/server"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun serveDishes(): Flux<Dish> {
        return this.kitchen.dishes()
    }



}