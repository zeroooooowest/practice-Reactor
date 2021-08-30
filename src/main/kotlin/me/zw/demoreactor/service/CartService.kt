package me.zw.demoreactor.service

import me.zw.demoreactor.model.Cart
import me.zw.demoreactor.model.CartItem
import me.zw.demoreactor.repository.CartRepository
import me.zw.demoreactor.repository.ItemRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CartService(
    private val itemRepository: ItemRepository,
    private val cartRepository: CartRepository,
) {

    fun addToCart(
        cartId: String,
        id: String
    ): Mono<Cart> {
        return cartRepository.findById(cartId)
            .defaultIfEmpty(Cart(cartId))
            .flatMap { cart ->
                cart.cartItems.find { cartItem ->
                    cartItem.item.id == id
                }?.let { cartItem ->
                    cartItem.increment()
                    Mono.just(cart)
                } ?: itemRepository.findById(id)
                    .map { CartItem(it) }
                    .doOnNext { cartItem ->
                        cart.cartItems.add(cartItem)
                    }.map { cartItem ->
                        cart
                    }
            }.flatMap {
                cartRepository.save(it)
            }
    }
}