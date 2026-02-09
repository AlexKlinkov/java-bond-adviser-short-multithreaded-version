package com.myprojects.java_bonds_advisor.mappers.response;

import com.myprojects.java_bonds_advisor.dto.output.BondUserInfo;
import com.myprojects.java_bonds_advisor.entities.Bond;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static com.myprojects.java_bonds_advisor.utils.CheckAndParseData.*;

@Slf4j
@Component
public class BondUserInfoResponse {

    /**
     * The method modifies an entity 'Bond' from BD into BondUserInfo (dto as answer for user)
     *
     * @param bond an entity from BD
     * @return information about bonds to user
     */
    public BondUserInfo mapToResponse(Bond bond) {
        var bondUserInfo = new BondUserInfo();
        bondUserInfo.setBondName(bond.getName());
        bondUserInfo.setBoardId(bond.getBoardId());
        bondUserInfo.setInitialBondPrice(parseBigDecimal(bond.getInitialFaceValue()) != null ?
                parseBigDecimal(bond.getInitialFaceValue()).setScale(2, RoundingMode.HALF_EVEN) : null);
        bondUserInfo.setCurrentBondPrice(bond.getCurrentBondPrice() != null ?
                bond.getCurrentBondPrice().setScale(2, RoundingMode.HALF_EVEN) : null);
        bondUserInfo.setFaceUnit(bond.getFaceUnit());
        bondUserInfo.setCouponValue(bond.getCouponValue() != null ?
                bond.getCouponValue().setScale(2, RoundingMode.HALF_EVEN) : null);
        bondUserInfo.setCouponPercent(bond.getCouponPercent() != null ?
                bond.getCouponPercent().setScale(2, RoundingMode.HALF_EVEN) : null);
        bondUserInfo.setCouponDate(dateTypeInMoscowFormat(bond.getCouponDate()));
        bondUserInfo.setCouponFrequency(bond.getCouponFrequency());
        // calculate approximately remaining quantity payments of coupon
        OffsetDateTime today =  OffsetDateTime.now(getZoneId());
        OffsetDateTime endDate = bond.getMatDate() == null ? bond.getBuyBackDate() : bond.getMatDate();
        double averageDaysAYear = 365.2425;
        int couponPaymentsFrequency  = bond.getCouponFrequency() != null ? bond.getCouponFrequency() : 1;
        long daysToRedemption = bond.getDaysToRedemption() != null ?
                bond.getDaysToRedemption() : (endDate != null ? ChronoUnit.DAYS.between(today, endDate) : 0);
        if (daysToRedemption != 0) {
            bondUserInfo.setRemainingNumberOfPayments(
                    (int) Math.ceil((daysToRedemption / averageDaysAYear * couponPaymentsFrequency))
            );
        }
        bondUserInfo.setAccruedCouponValue(bond.getAccruedCouponIncome() != null ?
                bond.getAccruedCouponIncome().setScale(2, RoundingMode.HALF_EVEN) : null);
        bondUserInfo.setOfferDate(dateTypeInMoscowFormat(bond.getOfferDate()));
        bondUserInfo.setYieldToOfferDate(bond.getYieldToOfferDate() != null ?
                bond.getYieldToOfferDate().setScale(2, RoundingMode.HALF_EVEN) : null);
        bondUserInfo.setBuyBackDate(dateTypeInMoscowFormat(bond.getBuyBackDate()));
        bondUserInfo.setYieldToCallOption(bond.getYieldToCallOption() != null ?
                bond.getYieldToCallOption().setScale(2, RoundingMode.HALF_EVEN) : null);
        bondUserInfo.setMatDate(dateTypeInMoscowFormat(bond.getMatDate()));
        bondUserInfo.setYieldToMatDate(bond.getYieldToMatDate() != null ?
                bond.getYieldToMatDate().setScale(2, RoundingMode.HALF_EVEN) : null);
        return bondUserInfo;
    }

}
