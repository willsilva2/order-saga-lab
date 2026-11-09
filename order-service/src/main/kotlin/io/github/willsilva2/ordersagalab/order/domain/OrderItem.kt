package io.github.willsilva2.ordersagalab.order.domain

import java.math.BigDecimal

class OrderItem(
    val sku: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
) {
    init {
        if (quantity <= 0) {
            throw InvalidOrderException("Item quantity must be greater than zero")
        }
    }
}