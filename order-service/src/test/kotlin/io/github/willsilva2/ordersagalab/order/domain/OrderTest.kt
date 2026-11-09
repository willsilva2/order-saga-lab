package io.github.willsilva2.ordersagalab.order.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderTest {

    @Test
    fun `should not create an order without items`() {
        assertThrows<InvalidOrderException> {
            Order.create(items = emptyList())
        }
    }
}