package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Auction addItemToAuction(String auctionId, Item item) {
        Optional<Auction> auctionOptional = auctionRepository.findById(auctionId);
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            item.setCreatedByUserId(username);     // lub ID
            item.setCreatedByUsername(username);
            auction.getItems().add(item);
            return auctionRepository.save(auction);
        }
        return null;
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


    public boolean deleteItemFromAuction(String auctionId, String itemName) {
        Optional<Auction> optionalAuction = auctionRepository.findById(auctionId);
        if (optionalAuction.isPresent()) {
            Auction auction = optionalAuction.get();
            List<Item> updatedItems = auction.getItems().stream()
                    .filter(item -> !item.getItemName().equals(itemName))
                    .toList();
            auction.setItems(new ArrayList<>(updatedItems));
            auctionRepository.save(auction);
            return true;
        }
        return false;
    }

    public Optional<Item> placeBid(String auctionId, String itemName, Bid bid) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);

        if (auctionOpt.isEmpty()) return Optional.empty();

        Auction auction = auctionOpt.get();
        Item item = auction.getItems().stream()
                .filter(i -> i.getItemName().equalsIgnoreCase(itemName))
                .findFirst()
                .orElse(null);

        if (item == null) return Optional.empty();

        // 🚫 Zablokuj licytowanie własnego przedmiotu
        if (item.getCreatedByUserId() != null && item.getCreatedByUserId().equals(bid.getUserId())) {
            return Optional.empty();
        }

        float highestBid = item.getBids().stream()
                .map(Bid::getAmount)
                .max(Float::compare)
                .orElse(item.getItemPrice());

        if (bid.getAmount() <= highestBid) return Optional.empty();

        item.getBids().add(bid);
        auctionRepository.save(auction);

        return Optional.of(item);
    }



}
