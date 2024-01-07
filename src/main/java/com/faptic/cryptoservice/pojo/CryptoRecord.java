package com.faptic.cryptoservice.pojo;

//you can create with this class objects that contain line of those fices csv files
public class CryptoRecord {

    // Constructors, getters, and setters

    // timestamp
    private long timestamp;

    //crypto name
    private String symbol;

    //crypto price
    private double price;

    public CryptoRecord(long timestamp, String symbol, double price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}