package com.myprojects.java_bonds_advisor.mappers;

import com.myprojects.java_bonds_advisor.dto.output.BondUserInfo;
import com.myprojects.java_bonds_advisor.mappers.response.BondUserInfoResponse;
import com.myprojects.java_bonds_advisor.testUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BondUserInfoResponseTest {

    private final BondUserInfoResponse mapper = new BondUserInfoResponse();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    void mapToResponseSuccessfulTest() {
        // Arrange
        var bond = testUtils.getTestBond();
        // Act
        BondUserInfo bondUserInfoAfterMap = mapper.mapToResponse(bond);
        // Assert
        assertEquals(bond.getName(), bondUserInfoAfterMap.getBondName());
        assertEquals(bond.getBoardId(), bondUserInfoAfterMap.getBoardId());
        assertEquals(bond.getInitialFaceValue() + ".00", bondUserInfoAfterMap.getInitialBondPrice().toString());
        assertEquals(bond.getCurrentBondPrice(), bondUserInfoAfterMap.getCurrentBondPrice());
        assertEquals(bond.getFaceUnit(), bondUserInfoAfterMap.getFaceUnit());
        assertEquals(bond.getCouponValue(), bondUserInfoAfterMap.getCouponValue());
        assertEquals(bond.getCouponPercent(), bondUserInfoAfterMap.getCouponPercent());
        assertEquals(bond.getCouponDate().format(formatter), bondUserInfoAfterMap.getCouponDate());
        assertEquals(bond.getCouponFrequency(), bondUserInfoAfterMap.getCouponFrequency());
        assertEquals(4, bondUserInfoAfterMap.getRemainingNumberOfPayments());
        assertEquals(BigDecimal.valueOf(21.25), bondUserInfoAfterMap.getAccruedCouponValue());
        assertEquals(bond.getOfferDate().format(formatter), bondUserInfoAfterMap.getOfferDate());
        assertEquals(bond.getYieldToOfferDate(), bondUserInfoAfterMap.getYieldToOfferDate());
        assertEquals(bond.getBuyBackDate().format(formatter), bondUserInfoAfterMap.getBuyBackDate());
        assertEquals(bond.getYieldToCallOption(), bondUserInfoAfterMap.getYieldToCallOption());
        assertEquals(bond.getMatDate().format(formatter), bondUserInfoAfterMap.getMatDate());
        assertEquals(bond.getYieldToMatDate(), bondUserInfoAfterMap.getYieldToMatDate());
    }
}
