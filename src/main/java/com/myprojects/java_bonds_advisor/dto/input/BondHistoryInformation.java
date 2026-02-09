package com.myprojects.java_bonds_advisor.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BondHistoryInformation {

    private String secId; // 1. Unique code representing a specific bond.
    private String boardId; // 2. Identifier, which points to an area, where these bonds are merchandising (TQOB, TQCB).
    private String tradeDate; // 3. Date on which the trade occurred.
    private String shortName; // 4. Short name or abbreviation of the security. (more human unlike ticket name).
    private String numTrades; // 5. Number of trades made for this security on the given trade date.
    private String value; // 6. Total value of all trades in the security, usually in the local currency.
    private String low; // 7. Lowest price of the security during the trading session.
    private String high; // 8. Highest price of the security during the trading session.
    private String close; // 9. Closing price of the security for the trading session.
    private String legalClosePrice; // 10. Min price of security during the last 10 minutes before market will be close.
    private String accInt; // 11. Accrued interest. Interest accrued but not yet paid for bonds.
    private String waPrice; // 12. Weighted average price of the trades during the session.
    private String yieldClose; // 13. Yield of the bond based on the closing price, typically expressed as a percentage.
    private String open; // 14. Opening price of the security for the trading session.
    private String volume; // 15. Total traded volume of the security, typically expressed in units or lots.
    private String marketPrice2; // 16. Calculated market price of the security, Method 2 (difficult formula)
    private String marketPrice3; // 17. Calculated market price of the security, Method 3 (difficult formula)
    private String admittedQuote; // 18. Price admitted for trading or official quoting.
    private String mp2ValTrd; // 19. Total value of trades calculated using MARKETPRICE2.
    private String marketPrice3TradesValue; // 20. Total value of trades calculated using MARKETPRICE3.
    private String admittedValue; // 21. Total value of trades admitted for trading.
    private String matDate; // 22. Maturity date. The date when the bond's principal amount is repaid to the holder.
    private String duration; // 23. Maturity date. The date when the bond's principal amount is repaid to the holder.
    private String yieldAtWap; // 24. Yield of the bond at the weighted average price.
    private String iriCpiClose; // 25. Close price from the IRICPI index.
    private String beiClose; // 26. Close price. BEI (Break-even Inflation) index, used to gauge inflation expectations.
    private String couponPercent; // 27. Coupon rate of the bond (percentage). It determines the annual interest payment.
    private String couponValue; // 28. Value of the coupon payment in the bond's currency.
    private String buyBackDate; // 29. The date when the issuer may buy back the bond, if applicable.
    private String lastTradeDate; // 30. The last date the bond is traded before maturity or de-listing.
    private String faceValue; // 31. Nominal value of the bond, typically the amount paid to the holder at maturity.
    private String currentCyId; // 32. The currency in which the bond is denominated (e.g., RUB, USD).
    private String cbrClose; // 33. Central Bank of Russia close price or a reference yield, if applicable.
    private String yieldToOffer; // 34. The yield of the bond if held to an offer date, often used in callable bonds.
    private String yieldLastCoupon; // 35. Yield calculated at the last coupon payment date.
    private String offerDate; // 36. The date when a bond is first offered or when a callable bond may be called.
    private String faceUnit; // 37. Indicates the currency or units for the bond's nominal value.
    private String tradingSession; // 38. The trading session during which the data was recorded (morning, day, evening).

}
