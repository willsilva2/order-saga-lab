package io.github.willsilva2.ordersagalab.order.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderTest {

    @Test
    fun `should not create an order without items`() {
        assertThrows<InvalidOrderException> {
            Order.create(items = emptyList())
        }
    }

    @Test
    fun `should create a pending order when items are provided`() {
        val order = Order.create(items = listOf(anItem()))

        assertEquals(OrderStatus.PENDING, order.status)
    }

    private fun anItem() = OrderItem()
}