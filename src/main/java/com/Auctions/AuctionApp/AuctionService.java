package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        auction.setTimestamp(LocalDateTime.now()); // Ustawiamy timestamp przy tworzeniu aukcji
        return auctionRepository.save(auction); // Zapisujemy aukcję w bazie
    }

    public Optional<Auction> getAuctionById(String auctionId) {
        return auctionRepository.findById(auctionId); // Pobieranie aukcji po ID
    }

    public Auction addItemToAuction(String auctionId, Item item) {
        Optional<Auction> auctionOptional = auctionRepository.findById(auctionId);
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            auction.getItems().add(item); // Dodajemy przedmiot do aukcji
            return auctionRepository.save(auction); // Zapisujemy zmodyfikowaną aukcję
        }
        return null; // Jeśli aukcja nie istnieje
    }

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll(); // Zwracamy wszystkie aukcje
    }
}
