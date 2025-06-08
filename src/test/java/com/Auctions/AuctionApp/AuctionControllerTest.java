package com.Auctions.AuctionApp;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AuctionControllerTest {

    private AutoCloseable closeable;
    private MockMvc mockMvc;

    @Mock
    private AuctionService auctionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuctionController auctionController;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(auctionController).build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testCreateAuction() throws Exception {
        AuctionRequest request = new AuctionRequest();
        request.setAuctionName("Test Auction");
        request.setCategory("Electronics");
        request.setDurationMinutes(60);
        request.setItemName("Phone");
        request.setItemDescription("Smartphone");
        request.setItemPrice(100.0);

        User user = new User();
        user.setId("user123");
        user.setUsername("john");

        when(jwtUtil.extractUsername("Bearer token")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(auctionService.createAuction(any(Auction.class))).thenReturn(new Auction());

        mockMvc.perform(post("/api/v1/auctions/create")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllAuctions() throws Exception {
        when(auctionService.getAllAuctions()).thenReturn(List.of(new Auction(), new Auction()));

        mockMvc.perform(get("/api/v1/auctions"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAuctionByIdFound() throws Exception {
        when(auctionService.getAuctionById("abc123")).thenReturn(Optional.of(new Auction()));

        mockMvc.perform(get("/api/v1/auctions/abc123"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAuctionByIdNotFound() throws Exception {
        when(auctionService.getAuctionById("notfound")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/auctions/notfound"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAuctionSuccess() throws Exception {
        when(auctionService.deleteAuction("abc123")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/auctions/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteAuctionNotFound() throws Exception {
        when(auctionService.deleteAuction("abc123")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/auctions/abc123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPlaceBidSuccess() throws Exception {
        User user = new User();
        user.setId("user456");

        Auction auction = new Auction();
        auction.setId("a1");
        auction.setCurrentBid(50);
        auction.setEndTime(LocalDateTime.now().plusMinutes(10));
        auction.setCreatedByUserId("otherUser");

        Bid bid = new Bid();
        bid.setAmount(60);

        when(jwtUtil.extractUsername("token")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(auctionRepository.findById("a1")).thenReturn(Optional.of(auction));

        mockMvc.perform(post("/api/v1/auctions/a1/bid")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bid)))
                .andExpect(status().isOk());
    }

    @Test
    public void testEditItemInAuctionSuccess() throws Exception {
        User user = new User();
        user.setId("user1");

        Auction auction = new Auction();
        auction.setId("a1");
        auction.setCreatedByUserId("user1");

        Auction updated = new Auction();
        updated.setItemName("Updated");
        updated.setItemDescription("Desc");
        updated.setItemPrice(123);

        when(jwtUtil.extractUsername("token")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(auctionRepository.findById("a1")).thenReturn(Optional.of(auction));

        mockMvc.perform(put("/api/v1/auctions/a1/item/edit")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updated)))
                .andExpect(status().isOk());
    }
}
