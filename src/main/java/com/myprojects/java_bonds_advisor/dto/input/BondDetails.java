package com.myprojects.java_bonds_advisor.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BondDetails {


    private String secId; // 1. Security code
    private String boardId; // 2. Identifier of place where these bonds are merchandising (TQOB, TQCB, and others)
    private String name; // 3. Full name
    private String shortName; // 4. Short name
    private String regNumber; // 5. State registration number
    private String isin; // 6. ISIN code
    private String issueDate; // 7. Trading start date
    private String matDate; // 8. Maturity date
    private String initialFaceValue; // 9. Initial face value
    private String faceUnit; // 10. Face value currency
    private String latName; // 11. English name
    private String startDateMoex; // 12. Trading start date on MOEX
    private String programRegistryNumber; // 13. State registration number of the bond program
    private String listLevel; // 14. Listing level
    private String daysToRedemption; // 15. Days to redemption
    private String issueSize; // 16. Issue size
    private String faceValue; // 17. Nominal value
    private String isQualifiedInvestors; // 18. For qualified investors only (0 - no, 1 -yes)
    private String couponFrequency; // 19. Coupon payment frequency per year
    private String couponDate; // 20. Coupon payment date
    private String couponPercent; // 21. Coupon rate, %
    private String couponValue; // 22. Coupon amount in face value currency
    private String typeName; // 23. Type/category of security
    private String group; // 24. Instrument type code
    private String type; // 25. Type of security
    private String groupName; // 26. Type of instrument
    private String emitterId; // 27. Issuer code
}
