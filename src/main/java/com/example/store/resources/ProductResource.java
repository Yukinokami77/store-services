package com.example.store.resources;

import com.example.store.models.Product;
import com.example.store.repositories.ProductRepository;
import com.example.store.services.ProductCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductRepository productRepository;
    @Inject
    ProductCacheService productCacheService;

    @GET
    public List<Product> getAllProducts() {
        return productRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Product getProduct(@PathParam("id") Long id) throws JsonProcessingException {
        String cachedProduct = productCacheService.getCachedProduct(id);
        if (cachedProduct != null) {
            return new ObjectMapper().readValue(cachedProduct, Product.class); // Преобразование JSON в объект
        }

        Product product = productRepository.findById(id);
        productCacheService.cacheProduct(id, new ObjectMapper().writeValueAsString(product)); // Кэшируем
        return product;
    }

    @POST
    @Transactional
    public Product createProduct(Product product) {
        productRepository.persist(product);
        return product;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Product updateProduct(@PathParam("id") Long id, Product product) {
        Product existing = productRepository.findById(id);
        if (existing == null) {
            throw new NotFoundException("Product not found");
        }

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setQuantity(product.getQuantity());
        existing.setPrice(product.getPrice());
        return existing;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void deleteProduct(@PathParam("id") Long id) {
        boolean deleted = productRepository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Product not found");
        }
    }
}


