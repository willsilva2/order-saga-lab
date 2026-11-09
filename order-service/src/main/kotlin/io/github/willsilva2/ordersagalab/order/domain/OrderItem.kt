package io.github.willsilva2.ordersagalab.order.domain

import java.math.BigDecimal

class OrderItem(
    val sku: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
)