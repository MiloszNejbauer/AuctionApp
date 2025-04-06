package com.Auctions.AuctionApp;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Auction {

    @Id
    private String id;
    private String auctionName;
    private LocalDateTime timestamp;

}
