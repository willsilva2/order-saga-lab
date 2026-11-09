package io.github.willsilva2.ordersagalab.order.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

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

    @Test
    fun `should calculate the total from quantity and unit price of each item`() {
        val order = Order.create(
            items = listOf(
                anItem(quantity = 2, unitPrice = BigDecimal("10.50")),
                anItem(quantity = 1, unitPrice = BigDecimal("5.00")),
            ),
        )

        assertEquals(0, BigDecimal("26.00").compareTo(order.total))
    }

    @Test
    fun `should not create an order with an item of zero or negative quantity`() {
        assertThrows<InvalidOrderException> {
            Order.create(items = listOf(anItem(quantity = 0)))
        }
        assertThrows<InvalidOrderException> {
            Order.create(items = listOf(anItem(quantity = -1)))
        }
    }

    @Test
    fun `should not create an order with an item of negative unit price`() {
        assertThrows<InvalidOrderException> {
            Order.create(items = listOf(anItem(unitPrice = BigDecimal("-1.00"))))
        }
    }

    @Test
    fun `should accept an item with zero unit price`() {
        val order = Order.create(items = listOf(anItem(unitPrice = BigDecimal.ZERO)))

        assertEquals(0, BigDecimal.ZERO.compareTo(order.total))
    }

    private fun anItem(
        sku: String = "SKU-1",
        quantity: Int = 1,
        unitPrice: BigDecimal = BigDecimal("10.00"),
    ) = OrderItem(sku, quantity, unitPrice)
}