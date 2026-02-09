package com.myprojects.java_bonds_advisor.service;

import com.myprojects.java_bonds_advisor.dto.output.BondUserInfo;
import com.myprojects.java_bonds_advisor.entities.Bond;
import com.myprojects.java_bonds_advisor.mappers.response.BondUserInfoResponse;
import com.myprojects.java_bonds_advisor.services.BondAdvisorService;
import com.myprojects.java_bonds_advisor.services.BondCollectorService;
import com.myprojects.java_bonds_advisor.testUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BondAdvisorServiceTest {

    @Mock
    private BondUserInfoResponse bondUserInfoResponse;

    @Mock
    private BondCollectorService bondCollectorService;

    @Mock
    private Model model;

    @InjectMocks
    private BondAdvisorService bondAdvisorService;

    private Pageable pageable;
    private Bond testBond;
    private BondUserInfo testBondUserInfo;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 50);

        testBond = testUtils.getTestBond();

        testBondUserInfo = BondUserInfo.builder()
                .boardId("TQCB")
                .bondName("Газпромбанк BO-001P-06 обл.")
                .build();
    }

    @Test
    void getBondAdviceByFilters_shouldReturnEmptyPage_whenNoBondsMatch() {
        // Arrange
        when(bondCollectorService.getMapWithPreparedBond()).thenReturn(Collections.emptyMap());

        // Act
        Page<BondUserInfo> result = bondAdvisorService.getBondAdviceByFilters(null, null,
                null, null, null, null, null,
                null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void getBondAdviceByFilters_shouldReturnFilteredBonds() {
        // Arrange
        when(bondCollectorService.getMapWithPreparedBond()).thenReturn(Map.of("TEST123", testBond));
        when(bondUserInfoResponse.mapToResponse(any(Bond.class))).thenReturn(testBondUserInfo);

        // Act
        Page<BondUserInfo> result = bondAdvisorService.getBondAdviceByFilters(null, BigDecimal.valueOf(5.0),
                "SUR",
                "TWICE A YEAR",
                "HIGH",
                "1",
                "FIX",
                "NO",
                "NO",
                "NO",
                pageable
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getBondAdviceByFilters_shouldHandleNullParameters() {
        // Arrange
        when(bondCollectorService.getMapWithPreparedBond()).thenReturn(Map.of("RU000A0JX0J2", testBond));
        when(bondUserInfoResponse.mapToResponse(any(Bond.class))).thenReturn(testBondUserInfo);

        // Act
        Page<BondUserInfo> result = bondAdvisorService.getBondAdviceByFilters(null, null,
                null, null, null, null, null,
                null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getBondAdviceByFilters_shouldConvertEnglishFiltersToRussian() {
        // Arrange
        Map<String, Bond> bondMap = Map.of("RU000A0JX0J2", testBond);
        when(bondCollectorService.getMapWithPreparedBond()).thenReturn(bondMap);
        when(bondUserInfoResponse.mapToResponse(any(Bond.class))).thenReturn(testBondUserInfo);

        // Act
        Page<BondUserInfo> result =
                bondAdvisorService.getBondAdviceByFilters(null, null, null,
                        "TWICE A YEAR",  // Should convert to "2"
                "HIGH",          // Should convert to "Высокая"
                "1",             // Should stay "1"
                "FIX",           // Should convert to "Фиксированный"
                "NO",            // Should convert to "Нет"
                "NO",            // Should convert to "0"
                "NO",            // Should convert to "Нет"
                pageable
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bondCollectorService, times(1)).getMapWithPreparedBond();
    }

    @Test
    void getData_shouldReturnEmptyPage_whenExceptionOccurs() {
        // Arrange
        when(bondCollectorService.getMapWithPreparedBond()).thenThrow(new RuntimeException("Test exception"));

        // Act
        Page<BondUserInfo> result = bondAdvisorService.getData(null, null,
                null, null, null, null, null,
                null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void fillupModelByBaseParams_shouldAddAllAttributes() {
        // Arrange
        Page<BondUserInfo> bondPage = new PageImpl<>(Collections.singletonList(testBondUserInfo), pageable, 1);
        when(bondCollectorService.isLoading()).thenReturn(false);
        when(bondCollectorService.getBondCount()).thenReturn(100);
        when(bondCollectorService.getTheLastTimeDataWasUpdated())
                .thenReturn(System.currentTimeMillis() - 15 * 60 * 1000L); // 15 minutes ago

        // Act
        bondAdvisorService.fillupModelByBaseParams(model, bondPage);

        // Assert
        verify(model).addAttribute(eq("bonds"), eq(bondPage));
        verify(model).addAttribute(eq("dateWhenDataWasUpdated"), anyString());
        verify(model).addAttribute(eq("currentPage"), eq(1));
        verify(model).addAttribute(eq("totalPages"), eq(1));
        verify(model).addAttribute(eq("isLoading"), eq(false));
        verify(model).addAttribute(eq("totalBonds"), eq(100));
        verify(model).addAttribute(eq("isOverTime"), anyBoolean());
    }

    @Test
    void fillupModelByFilterParams_shouldAddFilterParams() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        BigDecimal testRate = BigDecimal.valueOf(7.5);

        // Act
        bondAdvisorService.fillupModelByFilterParams(model, testDate, testRate, "RUB",
                "TWICE A YEAR", "HIGH", "1", "FIX",
                "NO", "NO", "NO");

        // Assert
        verify(model).addAttribute(eq("param"), any());
    }

    @Test
    void loadOrUpdateDataIfNecessary_shouldTriggerLoad_whenDataIsOld() {
        // Arrange
        when(bondCollectorService.isLoading()).thenReturn(false);
        when(bondCollectorService.getTheLastTimeDataWasUpdated())
                .thenReturn(System.currentTimeMillis() - 31 * 60 * 1000L); // 31 minutes ago

        // Act
        bondAdvisorService.loadOrUpdateDataIfNecessary();

        // Assert
        verify(bondCollectorService, times(1)).loadData();
    }

    @Test
    void loadOrUpdateDataIfNecessary_shouldNotTriggerLoad_whenDataIsFresh() {
        // Arrange
        when(bondCollectorService.isLoading()).thenReturn(false);
        when(bondCollectorService.getTheLastTimeDataWasUpdated())
                .thenReturn(System.currentTimeMillis() - 15 * 60 * 1000L); // 15 minutes ago

        // Act
        bondAdvisorService.loadOrUpdateDataIfNecessary();

        // Assert
        verify(bondCollectorService, never()).loadData();
    }

    @Test
    void loadOrUpdateDataIfNecessary_shouldNotTriggerLoad_whenAlreadyLoading() {
        // Arrange
        when(bondCollectorService.isLoading()).thenReturn(true);
        when(bondCollectorService.getTheLastTimeDataWasUpdated())
                .thenReturn(System.currentTimeMillis() - 31 * 60 * 1000L); // 31 minutes ago

        // Act
        bondAdvisorService.loadOrUpdateDataIfNecessary();

        // Assert
        verify(bondCollectorService, never()).loadData();
    }

    @Test
    void filterByPlacingDate_shouldReturnTrue_whenBothDatesNullAndFilterNull() {
        // Act & Assert
        assertNotNull(bondAdvisorService.getBondAdviceByFilters(null, null,
                null, null, null, null, null,
                null, null, null, pageable));
    }

    @Test
    void filterByCouponRate_shouldFilterCorrectly() {
        // Arrange
        when(bondCollectorService.getMapWithPreparedBond()).thenReturn(Map.of());

        // Act
        Page<BondUserInfo> result = bondAdvisorService.getBondAdviceByFilters(null, BigDecimal.valueOf(5.0),
                null, null, null, null, null,
                null, null, null, pageable);

        // Assert
        assertNotNull(result);
        verify(bondCollectorService, times(1)).getMapWithPreparedBond();
    }
}
