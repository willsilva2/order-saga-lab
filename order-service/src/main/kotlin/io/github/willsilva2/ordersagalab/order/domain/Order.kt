package io.github.willsilva2.ordersagalab.order.domain

class Order private constructor(val items: List<OrderItem>) {

    companion object {
        fun create(items: List<OrderItem>): Order {
            if (items.isEmpty()) {
                throw InvalidOrderException("An order must have at least one item")
            }
            return Order(items)
        }
    }
}