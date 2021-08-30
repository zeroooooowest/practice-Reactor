package me.zw.demoreactor.repository

import me.zw.demoreactor.model.Cart
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CartRepository : ReactiveCrudRepository<Cart, String> {
}