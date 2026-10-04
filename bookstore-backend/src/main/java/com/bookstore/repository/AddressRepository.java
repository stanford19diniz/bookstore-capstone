package com.bookstore.repository;

import com.bookstore.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserIdAndIsDeletedFalse(UUID userId);
    Optional<Address> findByIdAndUserIdAndIsDeletedFalse(UUID id, UUID userId);
}
