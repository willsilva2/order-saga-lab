package io.github.willsilva2.ordersagalab.order.application.port.inbound

import java.math.BigDecimal

data class OrderItemCommand(
    val sku: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
)

data class CreateOrderCommand(
    val items: List<OrderItemCommand>,
)