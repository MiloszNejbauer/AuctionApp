package com.Auctions.AuctionApp;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuctionRepository extends MongoRepository<Auction, String> {

    Optional<Auction> findByAuctionName(String auctionName);

    Optional<Auction> findByCategory (String category);

}
