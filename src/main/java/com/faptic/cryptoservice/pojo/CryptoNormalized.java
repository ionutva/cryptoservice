package com.faptic.cryptoservice.pojo;

public class CryptoNormalized {
    String cryptoName;
    double normalizedValue;

    public CryptoNormalized(String cryptoName, double normalizedValue) {
        this.cryptoName = cryptoName;
        this.normalizedValue = normalizedValue;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public double getNormalizedValue() {
        return normalizedValue;
    }

    public void setNormalizedValue(double normalizedValue) {
        this.normalizedValue = normalizedValue;
    }
}
