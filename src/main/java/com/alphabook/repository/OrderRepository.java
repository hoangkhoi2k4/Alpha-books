package com.alphabook.repository;

import com.alphabook.entity.Order;
import com.alphabook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}