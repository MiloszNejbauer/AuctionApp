package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuctionController(AuctionService auctionService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.auctionService = auctionService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint do tworzenia aukcji
    @PostMapping("/create")
    public ResponseEntity<Auction> createAuction(@RequestBody Auction auction, @RequestHeader("Authorization") String token) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = jwtUtil.extractUsername(token); // ✅ email z tokena
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));


        auction.setCreatedByUserId(user.getId());
        auction.setCreatedByUsername(user.getUsername());
        auction.setTimestamp(LocalDateTime.now());

        Auction createdAuction = auctionService.createAuction(auction);
        return new ResponseEntity<>(createdAuction, HttpStatus.CREATED);
    }

    // Endpoint do dodawania przedmiotów do aukcji
    @PostMapping("/{auctionId}/add-item")
    public ResponseEntity<Auction> addItemToAuction(
            @PathVariable String auctionId,
            @RequestBody Item item,
            @RequestHeader("Authorization") String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 🔧 usuń prefiks "Bearer "
        }

        String email = jwtUtil.extractUsername(token); // ✅ teraz token jest czysty
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        item.setCreatedByUserId(user.getId());
        item.setCreatedByUsername(user.getUsername());

        Auction updatedAuction = auctionService.addItemToAuction(auctionId, item);
        if (updatedAuction != null) {
            return ResponseEntity.ok(updatedAuction);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    // Endpoint do pobierania wszystkich aukcji
    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions() {
        return ResponseEntity.ok(auctionService.getAllAuctions());
    }

    // Endpoint do pobierania aukcji po ID
    @GetMapping("/{auctionId}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable String auctionId) {
        return auctionService.getAuctionById(auctionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Endpoint do pobierania przedmiotów z aukcji
    @GetMapping("/{auctionId}/items")
    public ResponseEntity<List<Item>> getItemsFromAuction(@PathVariable String auctionId) {
        return auctionService.getAuctionById(auctionId)
                .map(auction -> ResponseEntity.ok(auction.getItems()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Void> deleteAuction(@PathVariable String auctionId) {
        boolean deleted = auctionService.deleteAuction(auctionId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{auctionId}/items/by-name/{itemName}")
    public ResponseEntity<Void> deleteItemFromAuction(@PathVariable String auctionId, @PathVariable String itemName) {
        boolean deleted = auctionService.deleteItemFromAuction(auctionId, itemName);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{auctionId}/items/{itemName}/bid")
    public ResponseEntity<?> placeBid(
            @PathVariable String auctionId,
            @PathVariable String itemName,
            @RequestBody Bid bid) {

        Optional<Item> updatedItem = auctionService.placeBid(auctionId, itemName, bid);

        if (updatedItem.isPresent()) {
            return ResponseEntity.ok(updatedItem.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Bid too low or item not found");
        }

    }








}
