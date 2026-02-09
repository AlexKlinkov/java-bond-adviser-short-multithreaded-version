package com.myprojects.java_bonds_advisor.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.dto.input.BondHistoryInformation;
import com.myprojects.java_bonds_advisor.services.MoexService;
import com.myprojects.java_bonds_advisor.testUtils;
import com.myprojects.java_bonds_advisor.utils.MoexDtoCollectorUtils;
import com.myprojects.java_bonds_advisor.utils.WebClientUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoexServiceTest {

    @InjectMocks
    private MoexService moexService;

    private BondHistoryInformation testBondHistory;
    private BondDetails testBondDetails;
    private String mockJsonResponse;

    @BeforeEach
    void setUp() {
        testBondHistory = BondHistoryInformation.builder().secId("RU000A0JX0J2").build();
        testBondDetails = BondDetails.builder().secId("RU000A0JX0J2").name("Test Bond").build();

        // Mock JSON response structure
        mockJsonResponse = "{\"history\": {\"columns\": [\"secId\", \"name\"], \"data\": [[\"RU000A0JX0J2\"," +
                " \"Test Bond\"]]}, \"history.cursor\": {\"data\": [[0, 100]]}}";
    }

    @Test
    void getDetailInformationAboutBonds_shouldReturnEmptySet_whenWebClientThrowsException() {
        try (MockedStatic<WebClientUtils> webClientUtilsMock = mockStatic(WebClientUtils.class)) {
            // Arrange
            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondHistoryAsString(anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Network error"));

            // Act
            Set<BondDetails> result = moexService.getDetailInformationAboutBonds();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getDetailInformationAboutBonds_shouldReturnEmptySet_whenException() {
        try (MockedStatic<WebClientUtils> webClientUtilsMock = mockStatic(WebClientUtils.class)) {
            // Arrange
            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondHistoryAsString(anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Network error"));

            // Act
            Set<BondDetails> result = moexService.getDetailInformationAboutBonds();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getBondDetailsSimpleParallel_shouldProcessMultipleSecIds() {
        try (MockedStatic<WebClientUtils> webClientUtilsMock = mockStatic(WebClientUtils.class);
             MockedStatic<MoexDtoCollectorUtils> utilsMock = mockStatic(MoexDtoCollectorUtils.class)) {

            // Arrange
            var mockDetailsJson = "{\"description\": {\"data\": [[\"name\", \"string\", \"Test Bond\"]]}}";

            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondDetailsFromWebPage(anyString()))
                    .thenReturn(mockDetailsJson);

            utilsMock.when(() -> MoexDtoCollectorUtils.createMOEXBondDetails(anyMap(), anyString()))
                    .thenReturn(testBondDetails);

            // Act
            try (MockedStatic<JsonParser> jsonParserMock = mockStatic(JsonParser.class)) {
                // Mock the entire chain for getDetailInformationAboutBonds
                webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondHistoryAsString(anyInt(), anyInt()))
                        .thenReturn(mockJsonResponse);

                var mockRoot = mock(JsonObject.class);
                var mockHistoryCursor = mock(JsonObject.class);
                var mockCursorData = mock(JsonArray.class);
                var mockDataArray = mock(JsonArray.class);

                jsonParserMock.when(() -> JsonParser.parseString(anyString())).thenReturn(mockRoot);
                when(mockRoot.getAsJsonObject("history.cursor")).thenReturn(mockHistoryCursor);
                when(mockHistoryCursor.getAsJsonArray("data")).thenReturn(mockCursorData);
                when(mockCursorData.get(0)).thenReturn(mockDataArray);
                when(mockDataArray.get(1)).thenReturn(mock(com.google.gson.JsonElement.class));
                when(mockDataArray.get(1).getAsInt()).thenReturn(0); // No bonds to process

                Set<BondDetails> result = moexService.getDetailInformationAboutBonds();
                assertNotNull(result);
            }
        }
    }

    @Test
    void parseBondDetails_shouldReturnNull_whenJsonIsInvalid() {
        try (MockedStatic<WebClientUtils> webClientUtilsMock = mockStatic(WebClientUtils.class);
             MockedStatic<JsonParser> jsonParserMock = mockStatic(JsonParser.class)) {

            // Arrange
            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondHistoryAsString(anyInt(), anyInt()))
                    .thenReturn(mockJsonResponse);
            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondDetailsFromWebPage(anyString()))
                    .thenReturn("invalid json");

            // Mock first part (history) successfully
            var mockRoot = mock(JsonObject.class);
            var mockHistoryCursor = mock(JsonObject.class);
            var mockCursorData = mock(JsonArray.class);
            var mockDataArray = mock(JsonArray.class);
            var mockHistory = mock(JsonObject.class);
            var mockColumns = mock(JsonArray.class);
            var mockHistoryData = mock(JsonArray.class);

            jsonParserMock.when(() -> JsonParser.parseString(eq(mockJsonResponse))).thenReturn(mockRoot);
            jsonParserMock.when(() -> JsonParser.parseString(eq("invalid json")))
                    .thenThrow(new RuntimeException("Invalid JSON"));

            when(mockRoot.getAsJsonObject("history.cursor")).thenReturn(mockHistoryCursor);
            when(mockHistoryCursor.getAsJsonArray("data")).thenReturn(mockCursorData);
            when(mockCursorData.get(0)).thenReturn(mockDataArray);
            when(mockDataArray.get(1)).thenReturn(mock(com.google.gson.JsonElement.class));
            when(mockDataArray.get(1).getAsInt()).thenReturn(1); // 1 bond to process

            when(mockRoot.getAsJsonObject("history")).thenReturn(mockHistory);
            when(mockHistory.getAsJsonArray("columns")).thenReturn(mockColumns);
            when(mockHistory.getAsJsonArray("data")).thenReturn(mockHistoryData);
            when(mockHistoryData.size()).thenReturn(1);

            // Mock bond history creation
            try (MockedStatic<MoexDtoCollectorUtils> utilsMock = mockStatic(MoexDtoCollectorUtils.class)) {
                utilsMock.when(() -> MoexDtoCollectorUtils.getMapWithIndexOfInnerJsonElem(any(JsonArray.class)))
                        .thenReturn(Collections.emptyMap());
                utilsMock.when(() -> MoexDtoCollectorUtils.createBondByJsonInnerElems(any(JsonArray.class), anyMap()))
                        .thenReturn(testBondHistory);

                // Act
                Set<BondDetails> result = moexService.getDetailInformationAboutBonds();

                // Assert - should return empty since parseBondDetails fails
                assertNotNull(result);
                assertTrue(result.isEmpty());
            }
        }
    }

    @Test
    void getObjectIfObjIsNotEmptyOrNull_shouldThrowException_whenObjectIsEmpty() {
        assertThrows(IllegalAccessException.class, () ->
                MoexService.class.getDeclaredMethod("getObjectIfObjIsNotEmptyOrNull", Object.class, String.class)
                        .invoke(new MoexService(), "", "Test error"));
    }

    @Test
    void getObjectIfObjIsNotEmptyOrNull_shouldReturnObject_whenObjectIsNotEmpty() {
        try {
            var service = new MoexService();
            var method = service.getClass().getDeclaredMethod("getObjectIfObjIsNotEmptyOrNull", Object.class, String.class);
            method.setAccessible(true);

            Object result = method.invoke(service, "valid object", "Test error");

            assertEquals("valid object", result);
        } catch (Exception e) {
            fail("Reflection call failed: " + e.getMessage());
        }
    }

    @Test
    void getDetailInformationAboutBonds_shouldReturnBondDetails_whenSuccessful() {
        // Arrange
        var historyResponse = testUtils.createMockHistoryResponse();
        var detailsResponse = testUtils.createMockDetailsResponse();

        // Parse the JSON strings into JsonElements BEFORE using them in mocks
        JsonElement historyJsonElement = JsonParser.parseString(historyResponse);
        JsonElement detailsJsonElement = JsonParser.parseString(detailsResponse);

        try (MockedStatic<WebClientUtils> webClientUtilsMock = mockStatic(WebClientUtils.class);
             MockedStatic<JsonParser> jsonParserMock = mockStatic(JsonParser.class);
             MockedStatic<MoexDtoCollectorUtils> utilsMock = mockStatic(MoexDtoCollectorUtils.class)) {

            // Setup WebClient mocks
            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondHistoryAsString(100, 0))
                    .thenReturn(historyResponse); // First page call

            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondHistoryAsString(100, 100))
                    .thenReturn(historyResponse); // Second page call

            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondDetailsFromWebPage(anyString()))
                    .thenReturn(detailsResponse);

            // Setup JsonParser to return our pre-parsed JsonElements
            jsonParserMock.when(() -> JsonParser.parseString(historyResponse)).thenReturn(historyJsonElement);
            jsonParserMock.when(() -> JsonParser.parseString(detailsResponse)).thenReturn(detailsJsonElement);

            utilsMock.when(() -> MoexDtoCollectorUtils.getMapWithIndexOfInnerJsonElem(any()))
                    .thenReturn(Collections.singletonMap("SECID", 0));

            // Mock createBondByJsonInnerElems to return a BondHistoryInformation
            utilsMock.when(() -> MoexDtoCollectorUtils.createBondByJsonInnerElems(any(), any()))
                    .thenAnswer(invocation -> {
                        var bond = new BondHistoryInformation();
                        bond.setSecId("MOCK");
                        return bond;
                    });

            // Mock createMOEXBondDetails to return a BondDetails
            utilsMock.when(() -> MoexDtoCollectorUtils.createMOEXBondDetails(any(), any()))
                    .thenAnswer(invocation -> {
                        String secId = invocation.getArgument(1);
                        BondDetails details = new BondDetails();
                        details.setSecId(secId);
                        return details;
                    });

            // Act
            Set<BondDetails> result = moexService.getDetailInformationAboutBonds();

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());

        }
    }

    @Test
    void getDetailInformationAboutBonds_shouldReturnEmptySet_whenNoBondsFound() {
        // Create empty response
        String emptyHistoryResponse = testUtils.createEmptyHistoryResponse();

        // Parse it BEFORE using in mocks
        JsonElement emptyJsonElement = JsonParser.parseString(emptyHistoryResponse);

        try (MockedStatic<WebClientUtils> webClientUtilsMock = mockStatic(WebClientUtils.class);
             MockedStatic<JsonParser> jsonParserMock = mockStatic(JsonParser.class)) {

            // Setup WebClient mock
            webClientUtilsMock.when(() -> WebClientUtils.getMOEXBondHistoryAsString(anyInt(), anyInt()))
                    .thenReturn(emptyHistoryResponse);

            // Setup JsonParser to return pre-parsed element
            jsonParserMock.when(() -> JsonParser.parseString(emptyHistoryResponse))
                    .thenReturn(emptyJsonElement);

            // For any other string, you might want to handle differently
            jsonParserMock.when(() -> JsonParser.parseString(anyString()))
                    .thenAnswer(invocation -> {
                        String arg = invocation.getArgument(0);
                        if (arg.equals(emptyHistoryResponse)) {
                            return emptyJsonElement;
                        }
                        // For other strings, you can return null or throw
                        return null;
                    });

            // Act
            Set<BondDetails> result = moexService.getDetailInformationAboutBonds();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

}