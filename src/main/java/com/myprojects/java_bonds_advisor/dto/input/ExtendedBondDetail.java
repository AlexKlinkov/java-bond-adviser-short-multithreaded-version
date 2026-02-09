package com.myprojects.java_bonds_advisor.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExtendedBondDetail {

    private String secId; // 1. Security code
    private String creditRatingOfCompany; // 2. Credit rating reflects capability of company execute its mandatory
    private String offerDate; // 3. The data when bondholder can sell his bond to issuer before matDate/buyBackDate
    private String yieldToOfferDate; // 4. % of income to offerDate
    private String buyBackDate; // 5. The data when issuer can buy out your bonds
    private String yieldToCallOption; // 6. % of income to buyBackDate
    private String typeOfCouponValue; // 7. Fix/Float coupon value. Float can be depended on many of factors
    private String accruedCouponIncome; // 8. Accumulated sum of money to current date
    private String subordination; // 9. Yes/No. If 'No', bondholders are equal in their right to take yield
    private String amortization; // 10. Yes/No. Face value of obligation can be paid by part earlier then maturity date
    private String currentBondPrice; // 11. Approximate current price of a bond
    private String yieldToMatDate; // 12. % of income to maturity date
}
