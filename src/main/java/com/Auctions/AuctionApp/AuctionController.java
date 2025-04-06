package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @Autowired
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // Endpoint do tworzenia aukcji
    @PostMapping("/create")
    public ResponseEntity<Auction> createAuction(@RequestBody Auction auction) {
        Auction createdAuction = auctionService.createAuction(auction);
        return new ResponseEntity<>(createdAuction, HttpStatus.CREATED);
    }

    // Endpoint do dodawania przedmiotów do aukcji
    @PostMapping("/{auctionId}/add-item")
    public ResponseEntity<Auction> addItemToAuction(@PathVariable String auctionId, @RequestBody Item item) {
        Auction updatedAuction = auctionService.addItemToAuction(auctionId, item);
        if (updatedAuction != null) {
            return ResponseEntity.ok(updatedAuction);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Jeśli aukcja nie została znaleziona
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
}
