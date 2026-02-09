package com.myprojects.java_bonds_advisor.service;

import com.myprojects.java_bonds_advisor.entities.Bond;
import com.myprojects.java_bonds_advisor.services.TService;
import com.myprojects.java_bonds_advisor.utils.WebClientUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TServiceTest {

    @InjectMocks
    private TService tService;

    @Test
    void enrichBondDetailsInformation_shouldHandleEmptyInput() {
        // Arrange
        @SuppressWarnings("unchecked")
        Consumer<Map<String, Bond>> batchConsumer = mock(Consumer.class);

        // Act
        CompletableFuture<Void> future = tService.enrichBondDetailsInformation(Collections.emptyMap(), batchConsumer);

        // Assert
        assertNotNull(future);
        verify(batchConsumer, never()).accept(anyMap());
    }


    @Test
    void fetchBondDetails_shouldReturnNullForEmptyHtml() {
        try (MockedStatic<WebClientUtils> webClientMock = mockStatic(WebClientUtils.class)) {
            // Arrange
            webClientMock.when(() -> WebClientUtils.getTBWebPageAsStringByBondTicket(anyString())).thenReturn("");

            // Act
            var result = invokePrivateMethod("fetchBondDetails", "TEST123");

            // Assert
            assertNull(result);
        }
    }

    @Test
    void getCreditRatingOfCompany_shouldHandleDifferentCases() {
            // Arrange
            var mockDocument = mock(Document.class);
            var mockElements = mock(Elements.class);

            when(mockDocument.select(anyString())).thenReturn(mockElements);
            when(mockElements.eq(anyInt())).thenReturn(mockElements);

            // Test case 1: First element doesn't contain %
            when(mockElements.text()).thenReturn("Rating Info", "10.5%");
            var result = invokePrivateMethod("getCreditRatingOfCompany", mockDocument);
            assertEquals("10.5%", result);

            // Test case 2: Second element doesn't contain
            when(mockElements.text()).thenReturn("5%", "Offer Date", "Rating");
            result = invokePrivateMethod("getCreditRatingOfCompany", mockDocument);
            assertEquals("Rating", result);

            // Test case 3: Default case
            when(mockElements.text()).thenReturn("5%", "10.5%", "Final Rating");
            result = invokePrivateMethod("getCreditRatingOfCompany", mockDocument);
            assertEquals("Final Rating", result);

    }

    // Helper method to invoke private methods
    private <T> T invokePrivateMethod(String methodName, Object... args) {
        try {
            // Find the method based on argument types
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }

            var method = TService.class.getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(tService, args);
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke private method: " + methodName, e);
        }
    }
}