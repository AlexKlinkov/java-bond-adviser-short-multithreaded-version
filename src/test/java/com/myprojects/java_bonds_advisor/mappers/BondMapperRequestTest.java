package com.myprojects.java_bonds_advisor.mappers;

import com.myprojects.java_bonds_advisor.entities.Bond;
import com.myprojects.java_bonds_advisor.mappers.request.BondMapperRequest;
import com.myprojects.java_bonds_advisor.mappers.request.BondMapperRequestImpl;
import com.myprojects.java_bonds_advisor.testUtils;
import com.myprojects.java_bonds_advisor.utils.CheckAndParseData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BondMapperRequestTest {

    private final BondMapperRequest mapper = new BondMapperRequestImpl();

    @Test
    void mapToEntitySuccessfulTest() {

        Bond bondAfterMap = mapper.mapToEntity(testUtils.getTestBondDetails(), testUtils.getTestExtendedBondDetail());

        // Assert
        assertNotNull(bondAfterMap);
        // BondDetails
        assertEquals("TQCB", bondAfterMap.getBoardId());
        assertEquals("RU000A109874", bondAfterMap.getSecId());
        assertEquals("Bond Name", bondAfterMap.getName());
        assertEquals("Short Name", bondAfterMap.getShortName());
        assertEquals("regNumber", bondAfterMap.getRegNumber());
        assertEquals("isinCode", bondAfterMap.getIsin());
        assertEquals(CheckAndParseData.parseOffsetDateTime("2024-01-01"), bondAfterMap.getIssueDate());
        assertEquals(CheckAndParseData.parseOffsetDateTime("2025-01-01"), bondAfterMap.getMatDate());
        assertEquals(CheckAndParseData.parseOffsetDateTime("2024-06-01"), bondAfterMap.getBuyBackDate());
        assertEquals("1000", bondAfterMap.getInitialFaceValue());
        assertEquals("USD", bondAfterMap.getFaceUnit());
        assertEquals("Lat Name", bondAfterMap.getLatName());
        assertEquals(CheckAndParseData.parseOffsetDateTime("2024-02-01"), bondAfterMap.getStartDateMoex());
        assertEquals("program123", bondAfterMap.getProgramRegistryNumber());
        assertEquals("1", bondAfterMap.getListLevel());
        assertEquals(CheckAndParseData.parseInteger("30"), bondAfterMap.getDaysToRedemption());
        assertEquals(CheckAndParseData.parseBigDecimal("1000000"), bondAfterMap.getIssueSize());
        assertEquals(CheckAndParseData.parseBigDecimal("1000"), bondAfterMap.getFaceValue());
        assertEquals(CheckAndParseData.parseBigDecimal("985.25"), bondAfterMap.getCurrentBondPrice());
        assertEquals("1", bondAfterMap.getIsQualifiedInvestors());
        assertEquals(CheckAndParseData.parseInteger("4"), bondAfterMap.getCouponFrequency());
        assertEquals(CheckAndParseData.parseOffsetDateTime("2024-03-01"), bondAfterMap.getCouponDate());
        assertEquals(CheckAndParseData.parseBigDecimal("5.0"), bondAfterMap.getCouponPercent());
        assertEquals(CheckAndParseData.parseBigDecimal("50"), bondAfterMap.getCouponValue());
        assertEquals("Type Name", bondAfterMap.getTypeName());
        assertEquals("Group Code", bondAfterMap.getGroup());
        assertEquals("Type", bondAfterMap.getType());
        assertEquals("Group Name", bondAfterMap.getGroupName());
        assertEquals("emitter123", bondAfterMap.getEmitterId());
        // ExtendedBondDetail
        assertEquals("Высокий", bondAfterMap.getCreditRatingOfCompany());
        assertEquals(CheckAndParseData.parseOffsetDateTime("2024-02-01"), bondAfterMap.getOfferDate());
        assertEquals(CheckAndParseData.parseBigDecimal("15.6"), bondAfterMap.getYieldToOfferDate());
        assertEquals(CheckAndParseData.parseBigDecimal("14"), bondAfterMap.getYieldToCallOption());
        assertEquals(CheckAndParseData.parseBigDecimal("15.21"), bondAfterMap.getYieldToMatDate());
        assertEquals("Фиксированный", bondAfterMap.getTypeOfCouponValue());
        assertEquals(CheckAndParseData.parseBigDecimal("1000"), bondAfterMap.getAccruedCouponIncome());
        assertEquals("Нет", bondAfterMap.getSubordination());
        assertEquals("Нет", bondAfterMap.getAmortization());
    }
}
