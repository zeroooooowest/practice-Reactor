package me.zw.demoreactor.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import org.springframework.data.annotation.Id
import org.springframework.data.geo.Point
import java.time.LocalDate

class Item(
    @Id
    val id: String,

    val name: String,

    var description: String,

    var price: Double,
) {

    lateinit var distributorRegion: String

    lateinit var releaseDate: LocalDate

    var availableUnits: Int = 0

    lateinit var location: Point

    var active: Boolean = false

    override fun equals(o: Any?) = kotlinEquals(
        other = o,
        properties = arrayOf(Item::price, Item::id, Item::name, Item::description)
    )

    override fun hashCode() = kotlinHashCode(properties = arrayOf(Item::price, Item::id, Item::name, Item::description))

}