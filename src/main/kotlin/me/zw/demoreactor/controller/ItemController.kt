package me.zw.demoreactor.controller

import me.zw.demoreactor.model.Item
import me.zw.demoreactor.repository.ItemRepository
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@RestController
class ItemController(
    private val itemRepository: ItemRepository
) {

    @GetMapping("/api/items")
    fun findAll(): Flux<Item> {
        return itemRepository.findAll()
    }

    @GetMapping("/api/items/{id}")
    fun findOne(
        @PathVariable id: String
    ): Mono<EntityModel<Item>> {
        val controller = methodOn(ItemController::class.java)

        val selfLink = linkTo(controller.findOne(id))
            .withSelfRel()
            .andAffordance(controller.updateItem(Mono.empty(), id))
            .toMono()
        val aggregateLink = linkTo(controller.findAll())
            .withRel(IanaLinkRelations.ITEM)
            .toMono()

        return Mono.zip(itemRepository.findById(id), selfLink, aggregateLink)
            .map { EntityModel.of(it.t1, it.t2, it.t3) }
    }

    @PostMapping("/api/items")
    fun addNewItem(
        @RequestBody item: Mono<Item>
    ): Mono<ResponseEntity<Item>> {
        return item.flatMap { itemRepository.save(it) }
            .flatMap { savedItem ->
                Mono.just(
                    ResponseEntity.created(
                        URI.create("/api/items/${savedItem.id}")
                    ).body(savedItem)
                )
            }
    }

    @PutMapping("/api/items/{id}")
    fun updateItem(
        @RequestBody item: Mono<EntityModel<Item>>,
        @PathVariable id: String
    ): Mono<ResponseEntity<Item>> {
        return item
            .map { it.content!! }
            .map { Item(id, it.name, it.description, it.price) }
            .flatMap(itemRepository::save)
            .then(findOne(id))
            .map { entityModel ->
                ResponseEntity.noContent()
                    .location(
                        entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()
                    ).build()
            }
    }


}