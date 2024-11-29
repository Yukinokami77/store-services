package com.example.store.repositories;

import com.example.store.models.Product;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ProductRepositoryTest {

    @Inject
    ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    public void setUp() {
        // Подготовка тестового продукта
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(20.0);
        testProduct.setQuantity(100);

        // Убедимся, что база данных чиста перед каждым тестом
        productRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateProduct() {
        // Сохраняем продукт в репозитории
        productRepository.persist(testProduct);

        // Проверка, что продукт был успешно сохранён
        Product foundProduct = productRepository.findById(testProduct.getId());
        assertNotNull(foundProduct);
        assertEquals(testProduct.getName(), foundProduct.getName());
        assertEquals(testProduct.getDescription(), foundProduct.getDescription());
    }

    // Аналогичные аннотации @Transactional должны быть добавлены для других методов:
    @Test
    @Transactional
    public void testUpdateProduct() {
        productRepository.persist(testProduct);
        Product foundProduct = productRepository.findById(testProduct.getId());
        foundProduct.setPrice(30.0);
        productRepository.persist(foundProduct);

        Product updatedProduct = productRepository.findById(foundProduct.getId());
        assertEquals(30.0, updatedProduct.getPrice());
    }

    @Test
    @Transactional
    public void testDeleteProduct() {
        productRepository.persist(testProduct);
        Product foundProduct = productRepository.findById(testProduct.getId());
        assertNotNull(foundProduct);

        productRepository.delete(testProduct);

        Product deletedProduct = productRepository.findById(testProduct.getId());
        assertNull(deletedProduct);
    }
}

