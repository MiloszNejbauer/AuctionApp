package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AuctionRepository auctionRepository){
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Podany email jest już używany");
        }
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // <- kluczowy krok
        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        System.out.println("🔍 Sprawdzam użytkownika: " + email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ Hasło pasuje: " + passwordEncoder.matches(password, user.getPassword()));
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

}