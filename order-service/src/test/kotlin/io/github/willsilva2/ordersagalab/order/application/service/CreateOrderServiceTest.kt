package io.github.willsilva2.ordersagalab.order.application.service

import io.github.willsilva2.ordersagalab.order.application.port.inbound.CreateOrderCommand
import io.github.willsilva2.ordersagalab.order.application.port.inbound.OrderItemCommand
import io.github.willsilva2.ordersagalab.order.application.port.outbound.OrderRepository
import io.github.willsilva2.ordersagalab.order.domain.OrderStatus
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CreateOrderServiceTest {

    private val repository = mockk<OrderRepository>()
    private val service = CreateOrderService(repository)

    @Test
    fun `should create a pending order and save it`() {
        every { repository.save(any()) } just Runs
        val command = CreateOrderCommand(
            items = listOf(OrderItemCommand("SKU-1", 2, BigDecimal("10.50"))),
        )

        val order = service.create(command)

        assertEquals(OrderStatus.PENDING, order.status)
        verify(exactly = 1) { repository.save(order) }
    }
}