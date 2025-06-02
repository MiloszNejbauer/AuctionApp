package com.Auctions.AuctionApp;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Item {

    private String itemName;
    private float itemPrice;
    private String itemDescription;
    private String createdByUserId;
    private List<Bid> bids = new ArrayList<>();
    private String createdByUsername;

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public float getHighestBid() {
        return bids.stream()
                .map(Bid::getAmount)
                .max(Float::compare)
                .orElse(itemPrice); // jeśli brak ofert, zwraca cenę startową
    }

    @JsonProperty("currentBid")
    public float getCurrentBid() {
        return getHighestBid();
    }


}
