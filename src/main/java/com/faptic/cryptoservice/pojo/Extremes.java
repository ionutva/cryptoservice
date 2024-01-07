package com.faptic.cryptoservice.pojo;

public class Extremes {

    private String crypto;
    private long newest;
    private long oldest;
    private double maxPrice;
    private double minPrice;

    public Extremes(String crypto, long newest, long oldest, double maxPrice, double minPrice) {
        this.crypto = crypto;
        this.newest = newest;
        this.oldest = oldest;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;

    }

    public String getCrypto() {
        return crypto;
    }

    public void setCrypto(String crypto) {
        this.crypto = crypto;
    }

    public long getNewest() {
        return newest;
    }

    public void setNewest(long newest) {
        this.newest = newest;
    }

    public long getOldest() {
        return oldest;
    }

    public void setOldest(long oldest) {
        this.oldest = oldest;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }
}
