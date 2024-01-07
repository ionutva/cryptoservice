package com.faptic.cryptoservice.pojo;

public class ExtremesCrypto {

    private String value;
    private CryptoRecord cryptoRecord;
    // timestamp
    private long timestamp;

    //crypto name
    private String symbol;

    //crypto price
    private double price;

    public ExtremesCrypto(String value, CryptoRecord cryptoRecord) {
        this.value = value;
        this.cryptoRecord = cryptoRecord;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CryptoRecord getCryptoRecord() {
        return cryptoRecord;
    }

    public void setCryptoRecord(CryptoRecord cryptoRecord) {
        this.cryptoRecord = cryptoRecord;
    }


}
