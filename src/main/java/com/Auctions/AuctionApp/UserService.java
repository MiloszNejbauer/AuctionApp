package com.Auctions.AuctionApp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public UserService(UserRepository userRepository, AuctionRepository auctionRepository){
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

}
