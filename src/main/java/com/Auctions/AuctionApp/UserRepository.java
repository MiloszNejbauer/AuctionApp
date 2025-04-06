package com.Auctions.AuctionApp;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    // Sprawdzenie istnienia użytkownika na podstawie username
    boolean existsByUsername(String username);

    // Sprawdzenie istnienia użytkownika na podstawie email
    boolean existsByEmail(String email);

    void deleteUserById(String id);
}
