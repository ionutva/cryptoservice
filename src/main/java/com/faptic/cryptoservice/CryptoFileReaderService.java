package com.faptic.cryptoservice;

import com.faptic.cryptoservice.pojo.CryptoRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CryptoFileReaderService {

    List<String> excludedCryptos = Arrays.asList(/* "LTC" */);

    boolean isNotInExcluded(String crypto) {
        if (excludedCryptos.contains(crypto)) {
            return false;
        } else {
            return true;
        }
    }

    //get all cryptos name after reading the folder price in the project directory
    public List<String> getAllCryptos() {

        List<String> cryptos = new ArrayList<>();

        File folder = new File("..\\cryptoservice\\prices");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && isNotInExcluded(listOfFiles[i].getName().substring(0, listOfFiles[i].getName().indexOf("_")))) {
                cryptos.add(listOfFiles[i].getName().substring(0, listOfFiles[i].getName().indexOf("_")));
            }
        }
        return cryptos;

    }

    // get a list of records for a specific symbol of crypto
    public List<CryptoRecord> readCryptoRecords(String symbol) throws IOException {
        List<CryptoRecord> records = new ArrayList<>();
        Path path = Path.of("../cryptoservice/prices", symbol + "_values.csv");

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                CryptoRecord record = new CryptoRecord(
                        Long.parseLong(tokens[0]),
                        tokens[1],
                        Double.parseDouble(tokens[2])
                );
                records.add(record);
            }
        }

        return records;
    }

    public List<CryptoRecord> readAllCryptoRecords() throws IOException {

        List<CryptoRecord> allRecords = new ArrayList<>();
        for (String crypto : getAllCryptos()) {
            allRecords.addAll(readCryptoRecords(crypto));
        }

        return allRecords;

    }
}