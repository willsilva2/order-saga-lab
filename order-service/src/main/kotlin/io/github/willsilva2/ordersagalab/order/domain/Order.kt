package io.github.willsilva2.ordersagalab.order.domain

import java.math.BigDecimal

class Order private constructor(
    val id: OrderId,
    items: List<OrderItem>,
    status: OrderStatus,
) {
    init {
        if (items.isEmpty()) {
            throw InvalidOrderException("An order must have at least one item")
        }
    }

    val items: List<OrderItem> = items.toList()

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
        fun create(items: List<OrderItem>): Order =
            Order(OrderId.generate(), items, OrderStatus.PENDING)

        fun reconstitute(id: OrderId, items: List<OrderItem>, status: OrderStatus): Order =
            Order(id, items, status)
    }
}