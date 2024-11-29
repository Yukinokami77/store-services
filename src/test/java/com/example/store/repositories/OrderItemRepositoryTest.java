package com.example.store.repositories;

import com.example.store.models.OrderItem;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class OrderItemRepositoryTest {

    @Inject
    OrderItemRepository orderItemRepository;

    @Test
    @Transactional
    public void testCreateOrderItem() {
        // Создаем новый OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(10);
        orderItem.setSubtotal(100);
        // Сохраняем в репозитории
        orderItemRepository.persist(orderItem);

        // Проверяем, что OrderItem был сохранен
        assertNotNull(orderItem.getId());  // Проверка, что id был сгенерирован
    }

    @Test
    @Transactional
    public void testFindOrderItemById() {
        // Создаем и сохраняем OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(5);
        orderItem.setSubtotal(50);
        orderItemRepository.persist(orderItem);

        // Извлекаем его по id
        OrderItem foundOrderItem = orderItemRepository.findById(orderItem.getId());

        // Проверяем, что OrderItem был найден
        assertNotNull(foundOrderItem);
        assertEquals(orderItem.getId(), foundOrderItem.getId());
        assertEquals(5, foundOrderItem.getQuantity());
        assertEquals(50, foundOrderItem.getSubtotal());
    }

    @Test
    @Transactional
    public void testUpdateOrderItem() {
        // Создаем и сохраняем OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(3);
        orderItem.setSubtotal(30);
        orderItemRepository.persist(orderItem);

        // Обновляем данные
        orderItem.setQuantity(5);
        orderItem.setSubtotal(50);
        orderItemRepository.persist(orderItem);

        // Проверяем, что данные обновлены
        OrderItem updatedOrderItem = orderItemRepository.findById(orderItem.getId());
        assertNotNull(updatedOrderItem);
        assertEquals(5, updatedOrderItem.getQuantity());
        assertEquals(50, updatedOrderItem.getSubtotal());
    }

    @Test
    @Transactional
    public void testDeleteOrderItem() {
        // Создаем и сохраняем OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(2);
        orderItem.setSubtotal(20);
        orderItemRepository.persist(orderItem);

        // Удаляем OrderItem
        orderItemRepository.delete(orderItem);

        // Проверяем, что OrderItem был удален
        OrderItem deletedOrderItem = orderItemRepository.findById(orderItem.getId());
        assertEquals(null, deletedOrderItem);
    }
}
