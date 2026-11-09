package io.github.willsilva2.ordersagalab.order.domain

import java.math.BigDecimal

class Order private constructor(
    val items: List<OrderItem>,
    val status: OrderStatus,
) {
    val total: BigDecimal
        get() = items.fold(BigDecimal.ZERO) { acc, item ->
            acc + item.unitPrice.multiply(BigDecimal(item.quantity))
        }

    companion object {
        fun create(items: List<OrderItem>): Order {
            if (items.isEmpty()) {
                throw InvalidOrderException("An order must have at least one item")
            }
            return Order(items, OrderStatus.PENDING)
        }
    }
}