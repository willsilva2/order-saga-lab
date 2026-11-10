package io.github.willsilva2.ordersagalab.order.application.service

import io.github.willsilva2.ordersagalab.order.application.port.inbound.CreateOrderCommand
import io.github.willsilva2.ordersagalab.order.application.port.inbound.CreateOrderUseCase
import io.github.willsilva2.ordersagalab.order.application.port.outbound.OrderRepository
import io.github.willsilva2.ordersagalab.order.domain.Order
import io.github.willsilva2.ordersagalab.order.domain.OrderItem

class CreateOrderService(
    private val repository: OrderRepository,
) : CreateOrderUseCase {

    override fun create(command: CreateOrderCommand): Order {
        val items = command.items.map { OrderItem(it.sku, it.quantity, it.unitPrice) }
        val order = Order.create(items)
        repository.save(order)
        return order
    }
}