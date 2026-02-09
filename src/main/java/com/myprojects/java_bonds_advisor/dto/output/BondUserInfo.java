package com.myprojects.java_bonds_advisor.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BondUserInfo {
    private String bondName;
    private String boardId;
    private BigDecimal initialBondPrice;
    private BigDecimal currentBondPrice;
    private String faceUnit; // currency
    private BigDecimal couponValue;
    private BigDecimal couponPercent;
    private String couponDate;
    private Integer couponFrequency;
    private Integer remainingNumberOfPayments;
    private BigDecimal accruedCouponValue;
    private String offerDate; // when a bond buyer can sell bonds before maturity date will come
    private BigDecimal yieldToOfferDate; // income to offerDate
    private String buyBackDate; // when a bond issuer can buy out bonds before maturity date will come
    private BigDecimal yieldToCallOption; // income to buyBackDate
    private String matDate; // maturity date, when issuer has to return bond nominal and rest of coupon %
    private BigDecimal yieldToMatDate; // income to maturity date
}
