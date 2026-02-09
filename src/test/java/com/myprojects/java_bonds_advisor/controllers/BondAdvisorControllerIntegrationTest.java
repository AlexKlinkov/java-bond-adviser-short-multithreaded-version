package com.myprojects.java_bonds_advisor.controllers;

import com.myprojects.java_bonds_advisor.contollers.BondAdvisorController;
import com.myprojects.java_bonds_advisor.dto.output.BondUserInfo;
import com.myprojects.java_bonds_advisor.services.BondAdvisorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BondAdvisorController.class)
class BondAdvisorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BondAdvisorService bondAdvisorService;

    @Test
    void getBondAdviceByFilters_shouldReturnBondsView() throws Exception {
        // Arrange
        var bondPage = new PageImpl<>(Collections.singletonList(
                BondUserInfo.builder().boardId("boarId").build()), PageRequest.of(0, 50), 1);

        when(bondAdvisorService.getData(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(bondPage);

        // Act
        mockMvc.perform(get("/bonds/list")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("bonds"));

        // Assert
        verify(bondAdvisorService, times(1)).loadOrUpdateDataIfNecessary();
        verify(bondAdvisorService, times(1))
                .getData(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getHTMLExamplePageViewForSwaggerUI() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/example-response-for-swagger.html"))
                .andExpect(status().isOk())
                .andExpect(view().name("swagger-example-response"));
    }
}
