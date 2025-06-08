package com.Auctions.AuctionApp;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private AuctionService auctionService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    public void testGetAllAuctions() {
        when(auctionRepository.findAll()).thenReturn(Arrays.asList(new Auction(), new Auction()));

        List<Auction> result = auctionService.getAllAuctions();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAuctionById() {
        Auction auction = new Auction();
        when(auctionRepository.findById("1")).thenReturn(Optional.of(auction));

        Optional<Auction> result = auctionService.getAuctionById("1");
        assertTrue(result.isPresent());
        assertEquals(auction, result.get());
    }
}
