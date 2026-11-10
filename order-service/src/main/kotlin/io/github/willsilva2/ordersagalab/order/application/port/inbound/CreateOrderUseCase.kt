package io.github.willsilva2.ordersagalab.order.application.port.inbound

import io.github.willsilva2.ordersagalab.order.domain.Order

interface CreateOrderUseCase {
    fun create(command: CreateOrderCommand): Order
}