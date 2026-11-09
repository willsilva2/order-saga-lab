package io.github.willsilva2.ordersagalab.order.domain

import java.math.BigDecimal

class Order private constructor(
    val items: List<OrderItem>,
    status: OrderStatus,
) {
    var status: OrderStatus = status
        private set

    val total: BigDecimal
        get() = items.fold(BigDecimal.ZERO) { acc, item ->
            acc + item.unitPrice.multiply(BigDecimal(item.quantity))
        }

    fun confirm() {
        if (status == OrderStatus.CANCELLED) {
            throw InvalidOrderException("A cancelled order cannot be confirmed")
        }
        status = OrderStatus.CONFIRMED
    }

    fun cancel() {
        if (status == OrderStatus.CONFIRMED) {
            throw InvalidOrderException("A confirmed order cannot be cancelled")
        }
        status = OrderStatus.CANCELLED
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