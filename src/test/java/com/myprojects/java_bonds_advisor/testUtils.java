package com.myprojects.java_bonds_advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.dto.input.ExtendedBondDetail;
import com.myprojects.java_bonds_advisor.entities.Bond;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@UtilityClass
public class testUtils {

    public static Bond getTestBond() {
        return Bond.builder()
                .secId("RU000A0JX0J2")
                .boardId("TQCB")
                .name("Газпромбанк BO-001P-06 обл.")
                .shortName("ГПБ БО-001P-06")
                .regNumber("4B02-06-00123-P")
                .isin("RU000A0JX0J2")
                .issueDate(OffsetDateTime.of(2023, 5, 15, 10, 0, 0, 0, ZoneOffset.UTC))
                .matDate(OffsetDateTime.of(2028, 4, 15, 10, 0, 0, 0, ZoneOffset.UTC))
                .buyBackDate(OffsetDateTime.of(2026, 5, 15, 10, 0, 0, 0, ZoneOffset.UTC))
                .initialFaceValue("1000")
                .faceUnit("SUR")
                .latName("Gazprombank BO-001P-06")
                .startDateMoex(OffsetDateTime.of(2024, 5, 15, 10, 0, 0, 0, ZoneOffset.UTC))
                .programRegistryNumber("4B02-06-00123")
                .listLevel("1")
                .daysToRedemption(730)
                .issueSize(new BigDecimal("1000000000.00"))
                .faceValue(new BigDecimal("1000.00"))
                .currentBondPrice(new BigDecimal("1025.50"))
                .isQualifiedInvestors("0")
                .couponFrequency(2)
                .couponDate(OffsetDateTime.of(2024, 5, 15, 10, 0, 0, 0, ZoneOffset.UTC))
                .couponPercent(new BigDecimal("8.50"))
                .couponValue(new BigDecimal("42.50"))
                .typeName("Облигации банков")
                .group("stock")
                .type("bond")
                .groupName("Облигации")
                .emitterId("E12345")
                .creditRatingOfCompany("Высокая")
                .offerDate(OffsetDateTime.of(2027, 5, 15, 10, 0, 0, 0, ZoneOffset.UTC))
                .yieldToOfferDate(new BigDecimal("7.80"))
                .yieldToCallOption(new BigDecimal("8.20"))
                .yieldToMatDate(new BigDecimal("7.95"))
                .typeOfCouponValue("Фиксированный")
                .accruedCouponIncome(new BigDecimal("21.25"))
                .subordination("Нет")
                .amortization("Нет")
                .build();
    }

    public static BondDetails getTestBondDetails() {
        return BondDetails.builder()
                .boardId("TQCB")
                .secId("RU000A109874")
                .name("Bond Name")
                .shortName("Short Name")
                .regNumber("regNumber")
                .isin("isinCode")
                .issueDate("2024-01-01")
                .matDate("2025-01-01")
                .initialFaceValue("1000")
                .faceUnit("USD")
                .latName("Lat Name")
                .startDateMoex("2024-02-01")
                .programRegistryNumber("program123")
                .listLevel("1")
                .daysToRedemption("30")
                .issueSize("1000000")
                .faceValue("1000")
                .isQualifiedInvestors("1")
                .couponFrequency("4")
                .couponDate("2024-03-01")
                .couponPercent("5.0")
                .couponValue("50")
                .typeName("Type Name")
                .group("Group Code")
                .type("Type")
                .groupName("Group Name")
                .emitterId("emitter123")
                .build();
    }

    public static ExtendedBondDetail getTestExtendedBondDetail() {
        return ExtendedBondDetail.builder()
                .secId("RU000A109874")
                .creditRatingOfCompany("Высокий")
                .offerDate("2024-02-01")
                .yieldToOfferDate("15.6")
                .buyBackDate("2024-06-01")
                .yieldToCallOption("14")
                .typeOfCouponValue("Фиксированный")
                .accruedCouponIncome("1000")
                .subordination("Нет")
                .amortization("Нет")
                .currentBondPrice("985.25")
                .yieldToMatDate("15.21")
                .build();
    }

    // Helper methods to create JSON responses
    public static String createMockHistoryResponse() {
        var response = new JsonObject();

        // Create history object
        var history = new JsonObject();
        var columns = new JsonArray();
        columns.add("secid");
        history.add("columns", columns);

        var data = new JsonArray();
        var bondData = new JsonArray();
        bondData.add("RU000A0JX0J2");
        data.add(bondData);
        history.add("data", data);

        response.add("history", history);

        // Create cursor object
        var cursor = new JsonObject();
        var cursorData = new JsonArray();
        var cursorRow = new JsonArray();
        cursorRow.add(0);  // index
        cursorRow.add(1);  // total elements
        cursorData.add(cursorRow);
        cursor.add("data", cursorData);

        response.add("history.cursor", cursor);

        return response.toString();
    }

    public static String createEmptyHistoryResponse() {
        var response = new JsonObject();

        var history = new JsonObject();
        history.add("columns", new JsonArray());
        history.add("data", new JsonArray());
        response.add("history", history);

        var cursor = new JsonObject();
        var cursorData = new JsonArray();
        var cursorRow = new JsonArray();
        cursorRow.add(0);
        cursorRow.add(0);  // 0 total elements
        cursorData.add(cursorRow);
        cursor.add("data", cursorData);

        response.add("history.cursor", cursor);

        return response.toString();
    }

    public static String createMockDetailsResponse() {
        var response = new JsonObject();

        var description = new JsonObject();
        var data = new JsonArray();

        // Add some sample fields
        var nameField = new JsonArray();
        nameField.add("name");
        nameField.add("string");
        nameField.add("Test Bond");
        data.add(nameField);

        description.add("data", data);
        response.add("description", description);

        return response.toString();
    }
}
