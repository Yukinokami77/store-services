package com.example.store.resources;

import com.example.store.models.Order;
import com.example.store.models.OrderItem;
import com.example.store.models.Product;
import com.example.store.repositories.OrderRepository;
import com.example.store.repositories.OrderItemRepository;
import com.example.store.repositories.ProductRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    ProductRepository productRepository;

    @GET
    public List<Order> getAllOrders() {
        return orderRepository.listAll();
    }

    @POST
    @Transactional
    public Response createOrder(List<OrderItem> items) {
        if (items.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Order must have items").build();
        }

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setTotal(0);

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId());
            if (product == null || product.getQuantity() < item.getQuantity()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Product not available").build();
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            item.setSubtotal(item.getQuantity() * product.getPrice());
            item.setOrder(order);
            order.setTotal(order.getTotal() + item.getSubtotal());

            orderItemRepository.persist(item);
        }

        orderRepository.persist(order);
        return Response.ok(order).build();
    }
}

