package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Auction createAuction(Auction auction) {
        return auctionRepository.save(auction);
    }



    public Optional<Auction> getAuctionById(String auctionId) {
        return auctionRepository.findById(auctionId); // Pobieranie aukcji po ID
    }

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll(); // Zwracamy wszystkie aukcje
    }

    public boolean deleteAuction(String auctionId) {
        if (auctionRepository.existsById(auctionId)) {
            auctionRepository.deleteById(auctionId);
            return true;
        }
        return false;
    }

}
