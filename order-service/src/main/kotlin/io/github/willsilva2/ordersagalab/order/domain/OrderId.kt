package io.github.willsilva2.ordersagalab.order.domain

import java.util.UUID

data class OrderId(val value: UUID) {
    companion object {
        fun generate() = OrderId(UUID.randomUUID())
    }
}