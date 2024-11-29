package com.example.store.resources;

import com.example.store.models.Order;
import com.example.store.models.OrderItem;
import com.example.store.models.Product;
import com.example.store.models.ErrorResponse;
import com.example.store.repositories.OrderRepository;
import com.example.store.repositories.OrderItemRepository;
import com.example.store.repositories.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderResourceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderResource orderResource;

    private Product product;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        product = new Product();
        product.setId(1L);
        product.setPrice(10.0);
        product.setQuantity(10);
    }

    @Test
    void testCreateOrderSuccessfully() throws JsonProcessingException {
        Product product = new Product();
        product.setPrice(10.0); // Установим цену товара
        product.setQuantity(100); // Установим количество товара

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2); // Установим количество товаров в заказе

        OrderResource orderResource = new OrderResource();
        orderResource.productRepository = mock(ProductRepository.class);
        orderResource.productRepository.persist(product);
        orderResource.orderRepository = mock(OrderRepository.class);
        orderResource.orderItemRepository = mock(OrderItemRepository.class);

        List<OrderItem> items = List.of(item);
        Response response = orderResource.createOrder(items);

        // Проверяем, что итоговая сумма заказа правильная
        ObjectMapper objectMapper = new ObjectMapper();
        Order createdOrder = objectMapper.readValue((String) response.getEntity(), Order.class);

        // Проверяем, что сумма заказа правильная
         assertEquals(20.0, createdOrder.getTotal(), 0.01);
    }

    @Test
    void testCreateOrderWithEmptyItems() {
        // Пустой список элементов заказа
        List<OrderItem> items = List.of();

        // Создаем заказ и проверяем ответ
        Response response = orderResource.createOrder(items);

        // Проверка, что возвращен статус 400 и ошибка с сообщением "Order must have items"
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Order must have items", response.getEntity());
    }

    @Test
    void testCreateOrderWithUnavailableProduct() {
        // Создаем заказ с товаром, которого нет в наличии
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(20); // Запрашиваем больше, чем есть на складе

        // Мокаем поведение репозитория (товар найден, но его количество недостаточно)
        when(productRepository.findById(anyLong())).thenReturn(product);

        // Создаем заказ и проверяем ответ
        List<OrderItem> items = List.of(orderItem);
        Response response = orderResource.createOrder(items);

        // Проверяем, что ошибка была возвращена
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // Проверяем, что сообщение об ошибке корректно
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("Product not available", errorResponse.getMessage());
    }

    @Test
    void testCreateOrderWithProductNotFound() {
        // Создаем заказ с товаром, который не найден в базе
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);

        // Мокаем, что товар не найден
        when(productRepository.findById(anyLong())).thenReturn(null);

        // Создаем заказ и проверяем ответ
        List<OrderItem> items = List.of(orderItem);
        Response response = orderResource.createOrder(items);

        // Проверка, что возвращен статус 400 и ошибка с сообщением "Product not available"
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Product not available", response.getEntity());
    }
}
