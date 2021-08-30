package me.zw.demoreactor.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import me.zw.demoreactor.model.Cart
import me.zw.demoreactor.model.CartItem
import me.zw.demoreactor.model.Item
import me.zw.demoreactor.repository.CartRepository
import me.zw.demoreactor.repository.ItemRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
class InventoryServiceUnitTest(
) {

    // CUT (Class Under Test)
    private lateinit var inventoryService: InventoryService

    @MockkBean
    private lateinit var itemRepository: ItemRepository

    @MockkBean
    private lateinit var cartRepository: CartRepository

    @BeforeEach
    fun setUp() {
        val sampleItem = Item("item1", "TV tray", "Alf TV tray", 19.99)
        val sampleCartItem = CartItem(sampleItem)
        val sampleCart = Cart("My Cart", mutableListOf(sampleCartItem))

        every { cartRepository.findById(any<String>()) } returns Mono.empty()
        every { itemRepository.findById(any<String>()) } returns Mono.just(sampleItem)
        every { cartRepository.save(any<Cart>()) } returns Mono.just(sampleCart)

        inventoryService = InventoryService(itemRepository, cartRepository)
    }

    @Test
    fun addItemToEmptyCarShouldProduceOneCartItem() {
        inventoryService.addItemToCart("My Cart", "item1")
            .`as`(StepVerifier::create)
            .expectNextMatches { cart ->
                assertThat(cart.cartItems).extracting(CartItem::quantity)
                    .containsExactlyInAnyOrder(Tuple(1))

                assertThat(cart.cartItems).extracting(CartItem::item)
                    .containsExactly(Tuple(Item("item1", "TV tray", "Alf TV tray", 19.99)))

                true
            }
            .verifyComplete()
    }


}