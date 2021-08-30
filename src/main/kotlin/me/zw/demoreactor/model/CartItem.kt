package me.zw.demoreactor.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode

class CartItem(
    var item: Item
) {

    var quantity: Int = 1


    fun increment() {
        quantity++
    }

    fun decrement() {
        quantity--
    }

    override fun equals(o: Any?) = kotlinEquals(
        other = o,
        properties = arrayOf(CartItem::quantity, CartItem::item)
    )

    override fun hashCode() = kotlinHashCode(arrayOf(CartItem::quantity, CartItem::item))
}