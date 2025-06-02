package com.Auctions.AuctionApp;

import lombok.Data;

@Data
public class AuctionRequest {
    private String auctionName;
    private String category;
    private int durationMinutes; // np. 5, 30, 60, 1440

    public String getAuctionName() {
        return auctionName;
    }

    public void setAuctionName(String auctionName) {
        this.auctionName = auctionName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

}
