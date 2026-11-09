package io.github.willsilva2.ordersagalab.order.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    @ParameterizedTest
    @ValueSource(ints = [0, -1, -10])
    fun `should not create an order with an item of non-positive quantity`(quantity: Int) {
        assertThrows<InvalidOrderException> {
            Order.create(items = listOf(anItem(quantity = quantity)))
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

    @Test
    fun `should confirm a pending order`() {
        val order = Order.create(items = listOf(anItem()))

        order.confirm()

        assertEquals(OrderStatus.CONFIRMED, order.status)
    }

    @Test
    fun `should cancel a pending order`() {
        val order = Order.create(items = listOf(anItem()))

        order.cancel()

        assertEquals(OrderStatus.CANCELLED, order.status)
    }

    @Test
    fun `should not confirm a cancelled order`() {
        val order = Order.create(items = listOf(anItem()))

        order.cancel()

        assertThrows<InvalidOrderException> {
            order.confirm()
        }
    }

    @Test
    fun `should not cancel a confirmed order`() {
        val order = Order.create(items = listOf(anItem()))

        order.confirm()

        assertThrows<InvalidOrderException> {
            order.cancel()
        }
    }

    @Test
    fun `should keep a confirmed order confirmed when confirming it again`() {
        val order = Order.create(items = listOf(anItem()))

        order.confirm()
        order.confirm()

        assertEquals(OrderStatus.CONFIRMED, order.status)
    }

    @Test
    fun `should keep a cancelled order cancelled when cancelling it again`() {
        val order = Order.create(items = listOf(anItem()))

        order.cancel()
        order.cancel()

        assertEquals(OrderStatus.CANCELLED, order.status)
    }

    private fun anItem(
        sku: String = "SKU-1",
        quantity: Int = 1,
        unitPrice: BigDecimal = BigDecimal("10.00"),
    ) = OrderItem(sku, quantity, unitPrice)
}