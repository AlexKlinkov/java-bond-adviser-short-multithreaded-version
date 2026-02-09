package com.myprojects.java_bonds_advisor.services.filters;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterParams {
    private LocalDate placingUntilDate; // date when money can be return or be taken.
    private BigDecimal couponRateNoLessThan; // during a year.
    private String currency; // currency of bond
    private String couponFrequency; // frequency payments of coupon value a year (every month (12), once a quarter(4), twice a year(2))
    private String creditRatingOfCompany; // Высокая, Средняя, Низкая.
    private String listLevel; // 1, 2, 3.
    private String typeOfCouponValue; // Фиксированный, Плавающий.
    private String isSubordination; //  Да, Нет.
    private String isQualifiedInvestors; // Да, Нет.
    private String isAmortization; // Да, Нет.
}