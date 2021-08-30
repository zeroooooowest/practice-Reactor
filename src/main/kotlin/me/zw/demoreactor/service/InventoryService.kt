package me.zw.demoreactor.service

import me.zw.demoreactor.model.Cart
import me.zw.demoreactor.model.CartItem
import me.zw.demoreactor.model.Item
import me.zw.demoreactor.repository.CartRepository
import me.zw.demoreactor.repository.ItemRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors


class InventoryService(
    private val itemRepository: ItemRepository,
    private val cartRepository: CartRepository,
) {

    fun getCart(cartId: String): Mono<Cart?>? {
        return cartRepository.findById(cartId)
    }

    fun getInventory(): Flux<Item> {
        return itemRepository.findAll()
    }

    fun saveItem(newItem: Item): Mono<Item> {
        return itemRepository.save(newItem)
    }

    fun deleteItem(id: String): Mono<Void> {
        return itemRepository.deleteById(id)
    }

    fun addItemToCart(cartId: String, itemId: String): Mono<Cart> {
        return cartRepository.findById(cartId)
            .defaultIfEmpty(Cart(cartId)) //
            .flatMap { cart: Cart ->
//                cart.cartItems.stream()
//                    .filter { cartItem: CartItem -> cartItem.item.id == itemId }
//                    .findAny() //
//                    .map { cartItem: CartItem ->
//                        cartItem.increment()
//                        Mono.just(cart)
//                    } //
//                    .orElseGet {
//                        itemRepository.findById(itemId) //
//                            .map { item: Item? ->
//                                CartItem(
//                                    item!!
//                                )
//                            } //
//                            .map { cartItem: CartItem? ->
//                                cart.cartItems.add(cartItem!!)
//                                cart
//                            }
//                    }

                cart.cartItems.find { cartItem ->
                    cartItem.item.id == itemId
                }?.let { cartItem ->
                    cartItem.increment()
                    Mono.just(cart)
                } ?: itemRepository.findById(cartId)
                    .map { CartItem(it) }
                    .doOnNext { cartItem ->
                        cart.cartItems.add(cartItem)
                    }.map { cartItem ->
                        cart
                    }
            }
            .flatMap { cart: Cart ->
                cartRepository.save(
                    cart
                )
            }
    }

    fun removeOneFromCart(cartId: String, itemId: String): Mono<Cart?> {
        return cartRepository.findById(cartId)
            .defaultIfEmpty(Cart(cartId))
            .flatMap { cart: Cart ->
                cart.cartItems.stream()
                    .filter { cartItem: CartItem -> cartItem.item.id == itemId }
                    .findAny()
                    .map { cartItem: CartItem ->
                        cartItem.decrement()
                        Mono.just(cart)
                    } //
                    .orElse(Mono.empty())
            }
            .map { cart: Cart ->
                Cart(
                    cart.id, cart.cartItems.stream()
                        .filter { cartItem: CartItem -> cartItem.quantity > 0 }
                        .collect(Collectors.toList())
                )
            }
            .flatMap { cart: Cart ->
                cartRepository.save(
                    cart
                )
            }
    }


}
