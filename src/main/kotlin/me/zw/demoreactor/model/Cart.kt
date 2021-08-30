package me.zw.demoreactor.model

import org.springframework.data.annotation.Id

class Cart(
    @Id
    val id: String,

    val cartItems: MutableList<CartItem> = mutableListOf()
) {
}