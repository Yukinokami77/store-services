package com.example.store.resources;

import com.example.store.models.Product;
import com.example.store.repositories.ProductRepository;
import com.example.store.services.ProductCacheService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductResourceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCacheService productCacheService;

    @InjectMocks
    private ProductResource productResource;

    private Product product;

    @BeforeEach
    void setUp() {
        // Инициализация тестового продукта
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Description");
        product.setQuantity(10);
        product.setPrice(100.0);
    }

    @Test
    void testCreateProduct() {
        // Мокаем поведение persist, чтобы не делать ничего (так как это void метод)
        doNothing().when(productRepository).persist(any(Product.class));

        // Выполняем создание продукта
        Product result = productResource.createProduct(product);

        // Проверяем, что продукт был успешно создан
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).persist(product); // Проверяем, что метод persist был вызван
    }
    @Test
    void testUpdateProduct() {
        // Мокаем репозиторий
        when(productRepository.findById(1L)).thenReturn(product);

        // Новый продукт для обновления
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setQuantity(5);
        updatedProduct.setPrice(50.0);

        // Выполняем обновление
        Product result = productResource.updateProduct(1L, updatedProduct);

        // Проверяем, что продукт был обновлен
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProductNotFound() {
        // Мокаем репозиторий, чтобы возвращать null (продукт не найден)
        when(productRepository.findById(1L)).thenReturn(null);

        // Новый продукт для обновления
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");

        // Проверяем, что будет выброшено исключение
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productResource.updateProduct(1L, updatedProduct));
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testDeleteProduct() {
        // Мокаем репозиторий
        when(productRepository.deleteById(1L)).thenReturn(true);

        // Выполняем удаление
        productResource.deleteProduct(1L);

        // Проверяем, что метод удаления был вызван
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        // Мокаем репозиторий, чтобы возвращать false (продукт не найден)
        when(productRepository.deleteById(1L)).thenReturn(false);

        // Проверяем, что будет выброшено исключение
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productResource.deleteProduct(1L));
        assertEquals("Product not found", exception.getMessage());
    }
}
