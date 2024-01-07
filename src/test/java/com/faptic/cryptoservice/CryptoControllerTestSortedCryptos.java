package com.faptic.cryptoservice;

import com.faptic.cryptoservice.pojo.CryptoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

//this tests getSortedCryptos method from Crypto from CryptoController
@ExtendWith(MockitoExtension.class)
public class CryptoControllerTestSortedCryptos {

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
    void getSortedCryptosTest() throws IOException {
        // Arrange
        List<CryptoRecord> mockCryptoRecords = new ArrayList<>();
        mockCryptoRecords.add(new CryptoRecord(1641355200000L, "BTC", 66));
        mockCryptoRecords.add(new CryptoRecord(1941355200000L, "BTC", 69));

        when(cryptoFileReaderService.readAllCryptoRecords()).thenReturn(mockCryptoRecords);
        when(cryptoComputationService.sortCryptoData(anyList())).thenReturn(mockCryptoRecords);

        // Act
        List<CryptoRecord> sortedCryptos = cryptoController.getSortedCryptos();

        // Assert
        assertNotNull(sortedCryptos);
        assertEquals(mockCryptoRecords.size(), sortedCryptos.size());
        verify(cryptoFileReaderService).readAllCryptoRecords();
        verify(cryptoComputationService).sortCryptoData(anyList());
    }
}
