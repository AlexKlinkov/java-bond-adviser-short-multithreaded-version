package com.myprojects.java_bonds_advisor.entities;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class Bond implements Serializable {

    private String secId; // Unique ticket value
    private String boardId; // Identifier of merchandising place (TQOB, TQCB, and others)
    private String name; // Full name
    private String shortName; //
    private String regNumber; // State registration number
    private String isin; // ISIN code
    private OffsetDateTime issueDate; // Trading start date (date type for sorting/filtering)
    private OffsetDateTime matDate; // Maturity date
    private OffsetDateTime buyBackDate; // Date for yield calculation
    private String initialFaceValue; // currency
    private String faceUnit; // Face value currency
    private String latName; // English name
    private OffsetDateTime startDateMoex; // Trading start date on MOEX
    private String programRegistryNumber; // State registration number of the bond program
    private String listLevel; // 1 is the most reliable, 2 is less reliable, 3 is the most unreliable
    private Integer daysToRedemption; // Days to redemption
    private BigDecimal issueSize; // Issue size
    private BigDecimal faceValue; // Nominal value
    private BigDecimal currentBondPrice; // Current price of bond
    private String isQualifiedInvestors; // For qualified investors only value is 1 otherwise 0
    private Integer couponFrequency; // Coupon payment frequency per year
    private OffsetDateTime couponDate; // Coupon payment date
    private BigDecimal couponPercent; // Coupon rate, %
    private BigDecimal couponValue; // Coupon amount in face value currency
    private String typeName; // Type/category of security
    private String group; // Instrument type code
    private String type; // Type of security
    private String groupName; // Type of instrument
    private String emitterId; // Issuer code
    private String creditRatingOfCompany; // Высокий/Средний/Низкий
    private OffsetDateTime offerDate; // Date when bondholder can sell bond to issuer before matDate/buyBackDate
    private BigDecimal yieldToOfferDate; // % of income to offerDate (decimal for calculations)
    private BigDecimal yieldToCallOption; // % of income to buyBackDate (decimal for calculations)
    private BigDecimal yieldToMatDate; // % of income to maturity date (decimal for calculations)
    private String typeOfCouponValue; // Фиксированный/Плавающий
    private BigDecimal accruedCouponIncome; // Accumulated sum of money to current date
    private String subordination; // Да/Нет
    private String amortization; // Да/Нет
}
