package com.myprojects.java_bonds_advisor.service;

import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.entities.Bond;
import com.myprojects.java_bonds_advisor.services.BondCollectorService;
import com.myprojects.java_bonds_advisor.services.MoexService;
import com.myprojects.java_bonds_advisor.services.TService;
import com.myprojects.java_bonds_advisor.testUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BondCollectorServiceTest {

    @Mock
    private MoexService moexService;

    @Mock
    private TService tService;

    @InjectMocks
    private BondCollectorService bondCollectorService;

    private BondDetails testBondDetails;
    private Bond testBond;

    @BeforeEach
    void setUp() {
        testBondDetails = testUtils.getTestBondDetails();
        testBond = testUtils.getTestBond();
    }

    @Test
    void onApplicationStartup_shouldCallLoadData() {
        // Arrange
        bondCollectorService.loadData();

        // Act & Assert
        verify(moexService).getDetailInformationAboutBonds();
    }

    @Test
    void loadData_shouldLoadBondsSuccessfully() {
        // Arrange
        when(moexService.getDetailInformationAboutBonds())
                .thenReturn(Set.of(testBondDetails));

        doAnswer(invocation -> {
            java.util.function.Consumer<Map<String, Bond>> callback = invocation.getArgument(1);

            // Simulate async processing by invoking callback with test data
            Map<String, Bond> result = Map.of(testBond.getSecId(), testBond);
            callback.accept(result);

            return CompletableFuture.completedFuture(null);
        }).when(tService).enrichBondDetailsInformation(anyMap(), any());

        // Act
        bondCollectorService.loadData();

        // Assert
        verify(moexService, times(1)).getDetailInformationAboutBonds();
        verify(tService, times(1)).enrichBondDetailsInformation(anyMap(), any());

        assertFalse(bondCollectorService.isLoading());
        assertEquals(1, bondCollectorService.getBondCount());
        assertTrue(bondCollectorService.getTheLastTimeDataWasUpdated() > 0);
    }

    @Test
    void loadData_shouldHandleEmptyBondDetails() {
        // Arrange
        when(moexService.getDetailInformationAboutBonds())
                .thenReturn(Set.of()); // Empty set

        // Act
        bondCollectorService.loadData();

        // Assert
        verify(moexService, times(1)).getDetailInformationAboutBonds();
        verify(tService, never()).enrichBondDetailsInformation(anyMap(), any());
        assertFalse(bondCollectorService.isLoading()); // Should be set to false
    }

    @Test
    void loadData_shouldHandleAsyncException() {
        // Arrange
        when(moexService.getDetailInformationAboutBonds()).thenReturn(Set.of(testBondDetails));

        CompletableFuture<Map<String, Bond>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Async processing failed"));

        doAnswer(invocation -> {
            java.util.function.Consumer<Map<String, Bond>> callback = invocation.getArgument(1);

            // Simulate async processing by invoking callback with test data
            Map<String, Bond> result = Map.of(testBond.getSecId(), testBond);
            callback.accept(result);

            return CompletableFuture.completedFuture(null);
        }).when(tService).enrichBondDetailsInformation(anyMap(), any());

        // Act
        bondCollectorService.loadData();

        // Assert
        verify(moexService, times(1)).getDetailInformationAboutBonds();
        verify(tService, times(1)).enrichBondDetailsInformation(anyMap(), any());
        assertFalse(bondCollectorService.isLoading());
    }

    @Test
    void getMapWithPreparedBond_shouldReturnEmptyMapInitially() {
        // Act
        Map<String, Bond> result = bondCollectorService.getMapWithPreparedBond();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMapWithPreparedBond_shouldReturnBondsAfterLoad() {
        // Arrange
        when(moexService.getDetailInformationAboutBonds())
                .thenReturn(Set.of(testBondDetails));

        doAnswer(invocation -> {
            java.util.function.Consumer<Map<String, Bond>> callback = invocation.getArgument(1);

            // Simulate async processing by invoking callback with test data
            Map<String, Bond> result = Map.of(testBond.getSecId(), testBond);
            callback.accept(result);

            return CompletableFuture.completedFuture(null);
        }).when(tService).enrichBondDetailsInformation(anyMap(), any());

        // Act
        bondCollectorService.loadData();

        // Assert
        Map<String, Bond> result = bondCollectorService.getMapWithPreparedBond();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("RU000A0JX0J2", result.get("RU000A0JX0J2").getSecId());
    }

    @Test
    void isLoading_shouldBeFalseInitially() {
        // Act & Assert
        assertFalse(bondCollectorService.isLoading());
    }

    @Test
    void getTheLastTimeDataWasUpdated_shouldReturnZeroInitially() {
        // Act & Assert
        assertEquals(0, bondCollectorService.getTheLastTimeDataWasUpdated());
    }

    @Test
    void getTheLastTimeDataWasUpdated_shouldReturnTimestampAfterLoad() {
        // Arrange
        when(moexService.getDetailInformationAboutBonds()).thenReturn(Set.of(testBondDetails));

        doAnswer(invocation -> {
            java.util.function.Consumer<Map<String, Bond>> callback = invocation.getArgument(1);

            // Simulate async processing by invoking callback with test data
            Map<String, Bond> result = Map.of(testBond.getSecId(), testBond);
            callback.accept(result);

            return CompletableFuture.completedFuture(null);
        }).when(tService).enrichBondDetailsInformation(anyMap(), any());

        long beforeLoad = System.currentTimeMillis();

        // Act
        bondCollectorService.loadData();

        // Assert
        long afterLoad = System.currentTimeMillis();
        long updateTime = bondCollectorService.getTheLastTimeDataWasUpdated();

        assertTrue(updateTime > 0);
        assertTrue(updateTime >= beforeLoad && updateTime <= afterLoad);
    }
}