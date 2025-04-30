package com.small.ecommerce_chatbot.repository;

import com.small.ecommerce_chatbot.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
