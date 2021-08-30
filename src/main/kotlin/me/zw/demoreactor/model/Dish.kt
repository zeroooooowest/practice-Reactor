package me.zw.demoreactor.model

class Dish(
    var description: String,
) {
    var delivered: Boolean = false

    companion object {
        fun deliver(dish: Dish): Dish {
            val deliveredDish = Dish(dish.description)
            deliveredDish.delivered = true
            return deliveredDish
        }
    }
}
