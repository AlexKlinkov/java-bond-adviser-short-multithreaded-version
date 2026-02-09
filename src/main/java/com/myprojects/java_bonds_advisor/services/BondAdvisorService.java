package com.myprojects.java_bonds_advisor.services;

import com.myprojects.java_bonds_advisor.dto.output.BondUserInfo;
import com.myprojects.java_bonds_advisor.entities.Bond;
import com.myprojects.java_bonds_advisor.mappers.response.BondUserInfoResponse;
import com.myprojects.java_bonds_advisor.services.filters.FilterParams;
import com.myprojects.java_bonds_advisor.utils.CheckAndParseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class BondAdvisorService {

    private final int INTERVAL_MINUTES_BEFORE_UPDATE_DATA = 30;

    @Autowired
    private BondUserInfoResponse bondUserInfoResponse;

    @Autowired
    private BondCollectorService bondCollectorService;

    /**
     * This method gives information about bonds which is being merchanting at Moscow Exchange Rate area
     *
     * @return - a set of appropriate bonds by user corresponding params which was assign in filters
     */
    public Page<BondUserInfo> getBondAdviceByFilters(LocalDate placingUntilDate, BigDecimal couponRateNoLessThan, String currency,
                                                     String couponFrequency, String creditRatingOfCompany, String listLevel,
                                                     String typeOfCouponValue, String isSubordination,
                                                     String isQualifiedInvestors, String isAmortization, Pageable pageable) {

        // 1. change user format data in format which stored in BD
        if (currency != null) {
            if (currency.isBlank())
                currency = null;
        }
        if (couponFrequency != null) {
            switch (couponFrequency.toUpperCase()) {
                case "TWICE A YEAR" -> couponFrequency = "2";
                case "EVERY MONTH" -> couponFrequency = "12";
                case "ONCE A QUARTER" -> couponFrequency = "4";
                default -> couponFrequency = null;
            }
        }
        if (creditRatingOfCompany != null) {
            switch (creditRatingOfCompany.toUpperCase()) {
                case "HIGH" -> creditRatingOfCompany = "Высокая";
                case "AVERAGE" -> creditRatingOfCompany = "Средняя";
                case "LOW" -> creditRatingOfCompany = "Низкая";
                default -> creditRatingOfCompany = null;
            }
        }
        if (listLevel != null) {
            switch (listLevel.toUpperCase()) {
                case "1" -> listLevel = "1";
                case "2" -> listLevel = "2";
                case "3" -> listLevel = "3";
                default -> listLevel = null;
            }
        }
        if (typeOfCouponValue != null) {
            switch (typeOfCouponValue.toUpperCase()) {
                case "FIX" -> typeOfCouponValue = "Фиксированный";
                case "FLOAT" -> typeOfCouponValue = "Плавающий";
                default -> typeOfCouponValue = null;
            }
        }
        if (isSubordination != null) {
            switch (isSubordination.toUpperCase()) {
                case "NO" -> isSubordination = "Нет";
                case "YES" -> isSubordination = "Да";
                default -> isSubordination = null;
            }
        }
        if (isQualifiedInvestors != null) {
            switch (isQualifiedInvestors.toUpperCase()) {
                case "NO" -> isQualifiedInvestors = "0";
                case "YES" -> isQualifiedInvestors = "1";
                default -> isQualifiedInvestors = null;
            }
        }
        if (isAmortization != null) {
            switch (isAmortization.toUpperCase()) {
                case "NO" -> isAmortization = "Нет";
                case "YES" -> isAmortization = "Да";
                default -> isAmortization = null;
            }
        }

        // 2. prepare answer
        var finalListLevel = listLevel;
        var finalTypeOfCouponValue = typeOfCouponValue;
        var finalIsSubordination = isSubordination;
        var finalIsQualifiedInvestors = isQualifiedInvestors;
        var finalIsAmortization = isAmortization;
        var finalCreditRatingOfCompany = creditRatingOfCompany;
        var finalCouponFrequency = couponFrequency;
        var finalCurrency = currency;

        List<Bond> collectedBonds = bondCollectorService.getMapWithPreparedBond().values().stream()
                .filter(b -> b.getCurrentBondPrice() != null)
                .filter(b -> filterByPlacingDate(b, placingUntilDate))
                .filter(b -> filterByCouponRate(b, couponRateNoLessThan))
                .filter(b -> filterByField(b, Bond::getFaceUnit, finalCurrency))
                .filter(b -> filterByCouponFrequency(b, finalCouponFrequency))
                .filter(b -> filterByField(b, Bond::getCreditRatingOfCompany, finalCreditRatingOfCompany))
                .filter(b -> filterByField(b, Bond::getListLevel, finalListLevel))
                .filter(b -> filterByField(b, Bond::getTypeOfCouponValue, finalTypeOfCouponValue))
                .filter(b -> filterByField(b, Bond::getSubordination, finalIsSubordination))
                .filter(b -> filterByField(b, Bond::getIsQualifiedInvestors, finalIsQualifiedInvestors))
                .filter(b -> filterByField(b, Bond::getAmortization, finalIsAmortization))
                .sorted(Comparator.comparing(
                        Bond::getCouponPercent,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();

        // 3. create a PageImpl to provide pagination metadata
        List<BondUserInfo> pageContent = collectedBonds.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(bondUserInfoResponse::mapToResponse).toList();

        return new PageImpl<>(pageContent, pageable, collectedBonds.size());

    }

    // Helper methods:

    private boolean filterByPlacingDate(Bond bond, LocalDate placingUntilDate) {
        if (placingUntilDate == null) return true;

        var hasValidBuybackDate = bond.getBuyBackDate() != null &&
                !bond.getBuyBackDate().toLocalDate().isAfter(placingUntilDate);
        var hasValidMatDate = bond.getMatDate() != null && !bond.getMatDate().toLocalDate().isAfter(placingUntilDate);

        return hasValidBuybackDate || hasValidMatDate;
    }

    private boolean filterByCouponRate(Bond bond, BigDecimal minCouponRate) {
        if (minCouponRate == null) return true;
        return bond.getCouponPercent() != null
                && bond.getCouponPercent().compareTo(minCouponRate) >= 0;
    }

    private boolean filterByCouponFrequency(Bond bond, String frequency) {
        if (frequency == null || frequency.isEmpty()) return true;

        try {

            int freqValue = Integer.parseInt(frequency);
            return bond.getCouponFrequency() != null && bond.getCouponFrequency() >= freqValue;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean filterByField(Bond bond, Function<Bond, String> getter, String filterValue) {
        if (filterValue == null || filterValue.isEmpty()) return true;
        String fieldValue = getter.apply(bond);
        return fieldValue != null && fieldValue.equals(filterValue);
    }

    public Page<BondUserInfo> getData(LocalDate placingUntilDate, BigDecimal couponRateNoLessThan, String currency,
                                      String couponFrequency, String creditRatingOfCompany, String listLevel,
                                      String typeOfCouponValue, String isSubordination, String isQualifiedInvestors,
                                      String isAmortization, Pageable pageable) {
        Page<BondUserInfo> bondPage;
        try {
            bondPage = getBondAdviceByFilters(placingUntilDate, couponRateNoLessThan, currency, couponFrequency,
                    creditRatingOfCompany, listLevel, typeOfCouponValue, isSubordination, isQualifiedInvestors,
                    isAmortization, pageable
            );
        } catch (Exception e) {
            log.error("Error getting bond advice", e);
            bondPage = Page.empty(pageable);
        }
        return bondPage;
    }

    public void fillupModelByBaseParams(Model model, Page<BondUserInfo> bondPage) {

        var FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(CheckAndParseData.getZoneId());

        model.addAttribute("bonds", bondPage);
        model.addAttribute("dateWhenDataWasUpdated", LocalDateTime.now().format(FORMATTER));
        model.addAttribute("currentPage", bondPage.getTotalPages() != 0 ? (bondPage.getNumber() + 1) : 0);
        model.addAttribute("totalPages", bondPage.getTotalPages());
        model.addAttribute("isLoading", bondCollectorService.isLoading());
        model.addAttribute("totalBonds", bondCollectorService.getBondCount());
        model.addAttribute("isOverTime", ((System.currentTimeMillis() -
                bondCollectorService.getTheLastTimeDataWasUpdated()) / 60000.0) >= INTERVAL_MINUTES_BEFORE_UPDATE_DATA);
    }

    public void fillupModelByFilterParams(Model model, LocalDate placingUntilDate, BigDecimal couponRateNoLessThan,
                                          String currency, String couponFrequency, String creditRatingOfCompany,
                                          String listLevel, String typeOfCouponValue, String isSubordination,
                                          String isQualifiedInvestors, String isAmortization) {

        model.addAttribute("param", new FilterParams(placingUntilDate, couponRateNoLessThan, currency,
                couponFrequency, creditRatingOfCompany, listLevel, typeOfCouponValue, isSubordination,
                isQualifiedInvestors, isAmortization));
    }


    public void loadOrUpdateDataIfNecessary() {
        log.info("Page request - Data ready: {}, Is loading: {}, Time since last update: {} min",
                !bondCollectorService.isLoading(), bondCollectorService.isLoading(),
                (System.currentTimeMillis() - bondCollectorService.getTheLastTimeDataWasUpdated()) / 60000.0);

        long timeSinceLastUpdate = System.currentTimeMillis() - bondCollectorService.getTheLastTimeDataWasUpdated();
        boolean needsUpdate = timeSinceLastUpdate > (INTERVAL_MINUTES_BEFORE_UPDATE_DATA * 60 * 1000);

        // Trigger background load
        if (!bondCollectorService.isLoading() && needsUpdate) {
            log.info("Starting background data load...");
            try {
                bondCollectorService.loadData();
            } catch (Exception e) {
                log.error("Background load failed", e);
            }
        }
    }
}
