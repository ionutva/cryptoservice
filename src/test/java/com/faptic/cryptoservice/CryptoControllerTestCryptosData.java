
package com.faptic.cryptoservice;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.faptic.cryptoservice.pojo.CryptoRecord;
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

// this tests getCryptosData from CryptoController
@ExtendWith(MockitoExtension.class)
public class CryptoControllerTestCryptosData {

    @Mock
    private ApplicationContext appContext;

    @Mock
    private CryptoFileReaderService cryptoFileReaderService;

    @InjectMocks
    private CryptoController cryptoController;

    @BeforeEach
    void setUp() {
        // Mock the beans obtained from the application context
        when(appContext.getBean(CryptoFileReaderService.class)).thenReturn(cryptoFileReaderService);
    }

    @Test
    void getCryptosDataTest() throws IOException {
        // Arrange
        String symbol = "XRP";
        List<CryptoRecord> mockCryptoRecords = Arrays.asList(
                new CryptoRecord(1641355200000L, "XRP", 66),
                new CryptoRecord(1841355200000L, "XRP", 69));


        when(cryptoFileReaderService.readCryptoRecords(symbol)).thenReturn(mockCryptoRecords);

        // Act
        List<CryptoRecord> result = cryptoController.getCryptosData(symbol);

        // Assert
        assertNotNull(result);
        assertEquals(mockCryptoRecords.size(), result.size());
        assertEquals(mockCryptoRecords, result);
        verify(cryptoFileReaderService).readCryptoRecords(symbol);
    }
}
