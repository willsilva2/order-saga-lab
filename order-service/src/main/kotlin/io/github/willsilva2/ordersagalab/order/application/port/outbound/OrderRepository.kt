package io.github.willsilva2.ordersagalab.order.application.port.outbound

import io.github.willsilva2.ordersagalab.order.domain.Order
import io.github.willsilva2.ordersagalab.order.domain.OrderId

interface OrderRepository {
    fun save(order: Order)
    fun findById(id: OrderId): Order?
}