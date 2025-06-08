package com.Auctions.AuctionApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionController(AuctionService auctionService, UserRepository userRepository, JwtUtil jwtUtil, AuctionRepository auctionRepository) {
        this.auctionService = auctionService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.auctionRepository = auctionRepository;
    }

    // Endpoint do tworzenia aukcji
    @PostMapping("/create")
    public ResponseEntity<Auction> createAuction(@RequestBody AuctionRequest request, @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(request.getDurationMinutes());

        //pola aukcji
        Auction auction = new Auction();
        auction.setAuctionName(request.getAuctionName());
        auction.setCategory(request.getCategory());
        auction.setTimestamp(now);
        auction.setEndTime(endTime);
        auction.setCreatedByUserId(user.getId());
        auction.setCreatedByUsername(user.getUsername());

        //pola itemu w aukcji
        auction.setItemName(request.getItemName());
        auction.setItemDescription(request.getItemDescription());
        auction.setItemPrice(request.getItemPrice());
        auction.setCurrentBid(request.getItemPrice());

        Auction createdAuction = auctionService.createAuction(auction);
        return new ResponseEntity<>(createdAuction, HttpStatus.CREATED);
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

    //Endpoint do usuwania aukcji
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Void> deleteAuction(@PathVariable String auctionId) {
        boolean deleted = auctionService.deleteAuction(auctionId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Endpoint do licytowania
    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<?> placeBid(
            @PathVariable String auctionId,
            @RequestBody Bid bid,
            @RequestHeader("Authorization") String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (auctionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono aukcji");
        }

        Auction auction = auctionOpt.get();

        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aukcja zakończona");
        }

        if (auction.getCreatedByUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nie możesz licytować własnej aukcji");
        }

        double current = auction.getCurrentBid();
        if (bid.getAmount() <= current) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Zbyt niska oferta");
        }

        auction.setCurrentBid(bid.getAmount());
        auctionRepository.save(auction);

        return ResponseEntity.ok(auction);
    }

    //Endpoint do edycji itemu w aukcji
    @PutMapping("/{auctionId}/item/edit")
    public ResponseEntity<?> editItemInAuction(

            @PathVariable String auctionId,
            @RequestBody Auction updatedItem,
            @RequestHeader("Authorization") String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }


        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));


        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (auctionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono aukcji");
        }

        Auction auction = auctionOpt.get();

        if (!user.getId().equals(auction.getCreatedByUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nie jesteś właścicielem tego przedmiotu");
        }

        auction.setItemName(updatedItem.getItemName());
        auction.setItemDescription(updatedItem.getItemDescription());
        auction.setItemPrice(updatedItem.getItemPrice());

        auctionRepository.save(auction);

        return ResponseEntity.ok(auction);
    }
}
