package com.Auctions.AuctionApp;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String birthDate;
    @Indexed(unique = true)
    String email;
}
