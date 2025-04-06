package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

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
            throw new RuntimeException("User with this email already exists");
        }
        user.setActive(true);
        return userRepository.save(user);
    }
}

