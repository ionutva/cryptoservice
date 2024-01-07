package com.faptic.cryptoservice;

import com.faptic.cryptoservice.pojo.CryptoRecord;
import com.faptic.cryptoservice.pojo.Extremes;
import com.faptic.cryptoservice.pojo.ExtremesCrypto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/crypto")
public class CryptoController {

    @Autowired
    private ApplicationContext appContext;

    //this is built to keep track of IPs that were accesed the API in order to blcok many request
    static Map<Instant, String> ipTimeMap = new HashMap<Instant, String>();

    //This field is the maximum the requests allowed for one hour
    //Attention: do not make fast requests... WAIT FOR THE PAGE TO BE FULL LOADED in order to check number of request allowed
    //long MAX_REQUEST_FOR_ONE_HOUR = 5;
    long MAX_REQUEST_FOR_ONE_HOUR = 5000;


    //limit the page to be refresh for MAX_REQUEST_FOR_ONE_HOUR accesing http://localhost:8080/crypto/read/XRP
    boolean linitIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        //System.out.println(request.getRemoteAddr());
        String IP = request.getRemoteAddr();

        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);

        List<Instant> lastHourTimestamps = ipTimeMap.keySet().stream()
                .filter(timestamp -> timestamp.isAfter(oneHourAgo))
                .collect(Collectors.toList());

        long numberOfrequestPerIP = lastHourTimestamps.size();

        if (numberOfrequestPerIP >= MAX_REQUEST_FOR_ONE_HOUR) {
            return false;
        }

        Iterator<Instant> iterator = lastHourTimestamps.iterator();
        while (iterator.hasNext()) {
            Instant timestamp = iterator.next();
            if (timestamp.isBefore(oneHourAgo)) {
                iterator.remove();
            }
        }

        // Output the timestamps from the last hour
        lastHourTimestamps.forEach(System.out::println);

        ipTimeMap.put(Instant.now(), IP);

        return true;
    }

    // service to get a symbol values
    // access like http://localhost:8080/crypto/read/XRP
    // first point in the PDF
    @GetMapping("/read/{symbol}")
    public List<CryptoRecord> getCryptosData(@PathVariable String symbol) throws IOException {

        if (linitIP()) {
            System.out.println("Limit not exceeded");
        } else {
            System.out.println("Limit exceeded");
            return null;
        }
        CryptoFileReaderService readerService = appContext.getBean(CryptoFileReaderService.class);
        CryptoFileReaderService readValues = appContext.getBean(CryptoFileReaderService.class);

        return readValues.readCryptoRecords(symbol);
    }

    //return oldest/newest/min/max for all cryptos
    //http://localhost:8080/crypto/extremes
    //point 2 in the PDF
    @GetMapping("/extremes")
    public List<Extremes> getCryptoDataExtremes() throws IOException {

        if (linitIP()) {
            System.out.println("Limit not exceeded");
        } else {
            System.out.println("Limit exceeded");
            return null;
        }

        CryptoFileReaderService readerService = appContext.getBean(CryptoFileReaderService.class);
        CryptoComputationService computationBean = appContext.getBean(CryptoComputationService.class);
        //get all cryptos name after reading the folder price in the project directory
        List<String> cryptos = readerService.getAllCryptos();

        List<Extremes> extremes = new ArrayList<>();
        for (String crypto : cryptos) {
            Extremes extreme = computationBean.getExtremes(crypto);
            extremes.add(extreme);
        }
        return extremes;
    }

    //http://localhost:8080/crypto/sortednormalized
    //point 3 in the request PDF
    @GetMapping("/sortednormalized")
    public List<CryptoRecord> getSortedCryptos() {

        if (linitIP()) {
            System.out.println("Limit not exceeded");
        } else {
            System.out.println("Limit exceeded");
            return null;
        }

        CryptoComputationService computationBean = appContext.getBean(CryptoComputationService.class);
        try {
            List<CryptoRecord> cryptoList = appContext.getBean(CryptoFileReaderService.class).readAllCryptoRecords();
            return computationBean.sortCryptoData(cryptoList);
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
            return null;
        }
    }

    //access like http://localhost:8080/crypto/extremescrypto/ETH
    //point 4 in the request PDF
    @GetMapping("/extremescrypto/{symbol}")
    public List<ExtremesCrypto> getSortedCryptos(@PathVariable String symbol) throws IOException {

        if (linitIP()) {
            System.out.println("Limit not exceeded");
        } else {
            System.out.println("Limit exceeded");
            return null;
        }

        CryptoComputationService computationBean = appContext.getBean(CryptoComputationService.class);

        List<ExtremesCrypto> maxMinOldestNewest = computationBean.getMaxMinOldestNewestPriceFoSymbol(symbol);

        return maxMinOldestNewest;
    }

    //access like http://localhost:8080/crypto/highestnormalized/220114 patern yyMMdd
    //point 5 in PDF
    @GetMapping("/highestnormalized/{specificDay}")
    public String getCryptoWithHighestNormalizedRange(@PathVariable String specificDay) throws Exception {

        if (linitIP()) {
            System.out.println("Limit not exceeded");
        } else {
            System.out.println("Limit exceeded");
            return null;
        }

        CryptoComputationService computationBean = appContext.getBean(CryptoComputationService.class);

        return computationBean.getCryptoWithHighestNormalizedRange(specificDay);
    }

}


