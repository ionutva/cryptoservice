package com.faptic.cryptoservice;

import com.faptic.cryptoservice.pojo.CryptoNormalized;
import com.faptic.cryptoservice.pojo.CryptoRecord;
import com.faptic.cryptoservice.pojo.Extremes;
import com.faptic.cryptoservice.pojo.ExtremesCrypto;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CryptoComputationService {

    private final ApplicationContext appContext;

    public CryptoComputationService(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    // get the extremes value of timestamp and price for a specific symbol
    public Extremes getExtremes(String symbol) throws IOException {

        CryptoController cryptoController = appContext.getBean(CryptoController.class);
        List<CryptoRecord> cryptoList = cryptoController.getCryptosData(symbol);

        double minPrice = Double.MAX_VALUE;
        double maxPrice = -Double.MAX_VALUE;

        long minTimestamp = Long.MAX_VALUE;
        long maxTimestamp = -Long.MAX_VALUE;

        // Find min and max prices and min and max timestamps for a specific symbol
        for (CryptoRecord data : cryptoList) {

            if (data.getTimestamp() < minTimestamp) {
                minTimestamp = data.getTimestamp();
            }
            if (data.getTimestamp() > maxTimestamp) {
                maxTimestamp = data.getTimestamp();
            }

            if (data.getPrice() < minPrice) {
                minPrice = data.getPrice();
            }
            if (data.getPrice() > maxPrice) {
                maxPrice = data.getPrice();
            }
        }

        return new Extremes(symbol, minTimestamp, maxTimestamp, maxPrice, minPrice);
    }

    //utility method for sortCryptoData below for exposing an endpoint that will return a descending sorted list of all the cryptos,
    //comparing the normalized range (i.e. (max-min)/min)
    public double calculateNormalizedRange(List<CryptoRecord> cryptoList) {
        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0;

        for (CryptoRecord crypto : cryptoList) {
            if (crypto.getPrice() < minPrice) {
                minPrice = crypto.getPrice();
            }
            if (crypto.getPrice() > maxPrice) {
                maxPrice = crypto.getPrice();
            }
        }

        return (maxPrice - minPrice) / minPrice;
    }


    //sorted data for exposing an endpoint that will return a descending sorted list of all the cryptos,
    //comparing the normalized range (i.e. (max-min)/min)
    public List<CryptoRecord> sortCryptoData(List<CryptoRecord> cryptoList) {
        final double normalizedRange = calculateNormalizedRange(cryptoList);

        // Sort the list based on the normalized range without streams
        for (int i = 0; i < cryptoList.size() - 1; i++) {
            for (int j = i + 1; j < cryptoList.size(); j++) {
                CryptoRecord ci = cryptoList.get(i);
                CryptoRecord cj = cryptoList.get(j);
                if ((cj.getPrice() - normalizedRange) > (ci.getPrice() - normalizedRange)) {
                    Collections.swap(cryptoList, i, j);
                }
            }
        }

        return cryptoList;
    }


    // utility methods to get the Min Max Oldest Newst records for a symbol
    // utility for point 4 in PDF
    public List<ExtremesCrypto> getMaxMinOldestNewestPriceFoSymbol(String symbol) throws IOException {
        CryptoFileReaderService cryptoFileReaderService = appContext.getBean(CryptoFileReaderService.class);
        List<CryptoRecord> records = cryptoFileReaderService.readCryptoRecords(symbol);

        CryptoRecord max = null;
        CryptoRecord min = null;

        CryptoRecord oldest = null;
        CryptoRecord newest = null;

        double minPrice = Double.MAX_VALUE;
        double maxPrice = -Double.MAX_VALUE;

        long oldestTimestamp = Long.MAX_VALUE;
        long newestTimestamp = -Long.MAX_VALUE;

        for (CryptoRecord record : records) {

            // Find min and max prices and min and max timestamps for a specific symbol

            if (record.getPrice() > maxPrice) {
                maxPrice = record.getPrice();
                max = record;
            }
            if (record.getPrice() < minPrice) {
                minPrice = record.getPrice();
                min = record;
            }

            if (record.getTimestamp() < oldestTimestamp) {
                oldestTimestamp = record.getTimestamp();
                oldest = record;
            }
            if (record.getTimestamp() > newestTimestamp) {
                newestTimestamp = record.getTimestamp();
                newest = record;

            }
        }
        return Arrays.asList(new ExtremesCrypto("max", max), new ExtremesCrypto("min", min),
                new ExtremesCrypto("oldest", oldest), new ExtremesCrypto("newest", newest));
    }

    //this calculates the highest rages for all cryptos for a specific date, creates crypto -> highest normalized value
    //and return the crypto name from that List with highest normalized value for that specific date
    public String getCryptoWithHighestNormalizedRange(String specificDay) throws Exception {

        CryptoFileReaderService applicationContext = appContext.getBean(CryptoFileReaderService.class);
        List<String> cryptos = applicationContext.getAllCryptos();
        List<CryptoNormalized> normalizedCryptos = new ArrayList<>();
        for (String crypto : cryptos) {

            List<CryptoRecord> records = new ArrayList<>();
            Path path = Path.of("..\\cryptoservice\\prices", crypto + "_values.csv");

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                reader.readLine(); // Skip header line
                while ((line = reader.readLine()) != null) {

                    String[] tokens = line.split(",");
                    long timestamp = Long.parseLong(tokens[0]);
                    Date date = new Date(timestamp);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                    Date parameterDate = dateFormat.parse(specificDay);
                    if (isSameDay(date, parameterDate)) {
                        CryptoRecord record = new CryptoRecord(
                                Long.parseLong(tokens[0]),
                                tokens[1],
                                Double.parseDouble(tokens[2])
                        );
                        records.add(record);
                    }
                }
            }

            double minPrice = Double.MAX_VALUE;
            double maxPrice = Double.MIN_VALUE;
            for (CryptoRecord data : records) {
                if (data.getPrice() < minPrice) minPrice = data.getPrice();
                if (data.getPrice() > maxPrice) maxPrice = data.getPrice();
            }

            double highestNormalizedRange = -1;
            for (CryptoRecord data : records) {
                double normalizedRange = (data.getPrice() - minPrice) / (maxPrice - minPrice);
                if (normalizedRange > highestNormalizedRange) {
                    highestNormalizedRange = normalizedRange;

                }
            }
            normalizedCryptos.add(new CryptoNormalized(crypto, highestNormalizedRange));
        }
        long normalizedvalue = -1;
        String cryptoWithHighestNormalizedRange = "";
        for (CryptoNormalized normalizedCrypto : normalizedCryptos) {
            if (normalizedvalue < normalizedCrypto.getNormalizedValue()) {
                cryptoWithHighestNormalizedRange = normalizedCrypto.getCryptoName();
            }
        }

        return cryptoWithHighestNormalizedRange;
    }

    //check if two dates are the same
    private boolean isSameDay(Date date1, Date date2) {
        // Implement logic to check if date1 and date2 are the same day
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}



