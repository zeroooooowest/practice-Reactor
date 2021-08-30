package me.zw.demoreactor.repository

import me.zw.demoreactor.model.Item
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ItemRepository : ReactiveCrudRepository<Item, String> {

    fun findByNameContaining(partialName: String): Flux<Item>
}