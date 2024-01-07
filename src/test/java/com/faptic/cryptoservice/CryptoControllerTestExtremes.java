package com.faptic.cryptoservice;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.faptic.cryptoservice.pojo.CryptoRecord;
import com.faptic.cryptoservice.pojo.Extremes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// this tests getCryptoDataExtremes from CryptoController
@ExtendWith(MockitoExtension.class)
public class CryptoControllerTestExtremes {

    @Mock
    private ApplicationContext appContext;

    @Mock
    private CryptoFileReaderService cryptoFileReaderService;

    @Mock
    private CryptoComputationService cryptoComputationService;

    @InjectMocks
    private CryptoController cryptoController;

    @BeforeEach
    void setUp() {
        // Mock the beans obtained from the application context
        when(appContext.getBean(CryptoFileReaderService.class)).thenReturn(cryptoFileReaderService);
        when(appContext.getBean(CryptoComputationService.class)).thenReturn(cryptoComputationService);
    }

    @Test
    void getCryptoDataExtremesTest() throws IOException {
        // Arrange
        List<String> mockCryptoNames = Arrays.asList("BTC", "ETH");

        Extremes btcExtremes = new Extremes("BTC", 1641355200000L, 1541355200000L,
                66, 55);
        Extremes ethExtremes = new Extremes("ETH", 1841355200000L, 1441355200000L,
                66, 55);

        when(cryptoFileReaderService.getAllCryptos()).thenReturn(mockCryptoNames);
        when(cryptoComputationService.getExtremes("BTC")).thenReturn(btcExtremes);
        when(cryptoComputationService.getExtremes("ETH")).thenReturn(ethExtremes);

        // Act
        List<Extremes> extremesList = cryptoController.getCryptoDataExtremes();

        // Assert
        assertNotNull(extremesList);
        assertEquals(2, extremesList.size());
        assertTrue(extremesList.contains(btcExtremes));
        assertTrue(extremesList.contains(ethExtremes));
    }
}
