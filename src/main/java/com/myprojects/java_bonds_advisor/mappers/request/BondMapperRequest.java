package com.myprojects.java_bonds_advisor.mappers.request;

import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.dto.input.ExtendedBondDetail;
import com.myprojects.java_bonds_advisor.entities.Bond;
import com.myprojects.java_bonds_advisor.utils.CheckAndParseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {CheckAndParseData.class})
public interface BondMapperRequest {

    @Mapping(target = "secId", expression = "java(bondDetails.getSecId())")
    @Mapping(target = "boardId", expression = "java(bondDetails.getBoardId())")
    @Mapping(target = "name", expression = "java(bondDetails.getName())")
    @Mapping(target = "shortName", expression = "java(bondDetails.getShortName())")
    @Mapping(target = "regNumber", expression = "java(bondDetails.getRegNumber())")
    @Mapping(target = "isin", expression = "java(bondDetails.getIsin())")
    @Mapping(target = "issueDate", expression = "java(CheckAndParseData.parseOffsetDateTime(bondDetails.getIssueDate()))")
    @Mapping(target = "matDate", expression = "java(CheckAndParseData.parseOffsetDateTime(bondDetails.getMatDate()))")
    @Mapping(target = "buyBackDate", expression = "java(CheckAndParseData.parseOffsetDateTime((extendedBondDetail.getBuyBackDate())))")
    @Mapping(target = "initialFaceValue", expression = "java(bondDetails.getInitialFaceValue())")
    @Mapping(target = "faceUnit", expression = "java(bondDetails.getFaceUnit())")
    @Mapping(target = "latName", expression = "java(bondDetails.getLatName())")
    @Mapping(target = "startDateMoex", expression = "java(CheckAndParseData.parseOffsetDateTime(bondDetails.getStartDateMoex()))")
    @Mapping(target = "programRegistryNumber", expression = "java(bondDetails.getProgramRegistryNumber())")
    @Mapping(target = "listLevel", expression = "java(bondDetails.getListLevel())")
    @Mapping(target = "daysToRedemption", expression = "java(CheckAndParseData.parseInteger(bondDetails.getDaysToRedemption()))")
    @Mapping(target = "issueSize", expression = "java(CheckAndParseData.parseBigDecimal(bondDetails.getIssueSize()))")
    @Mapping(target = "faceValue", expression = "java(CheckAndParseData.parseBigDecimal(bondDetails.getFaceValue()))")
    @Mapping(target = "isQualifiedInvestors", expression = "java(bondDetails.getIsQualifiedInvestors())")
    @Mapping(target = "couponFrequency", expression = "java(CheckAndParseData.parseInteger(bondDetails.getCouponFrequency()))")
    @Mapping(target = "couponDate", expression = "java(CheckAndParseData.parseOffsetDateTime(bondDetails.getCouponDate()))")
    @Mapping(target = "couponPercent", expression = "java(CheckAndParseData.parseBigDecimal(bondDetails.getCouponPercent()))")
    @Mapping(target = "couponValue", expression = "java(CheckAndParseData.parseBigDecimal(bondDetails.getCouponValue()))")
    @Mapping(target = "typeName", expression = "java(bondDetails.getTypeName())")
    @Mapping(target = "group", expression = "java(bondDetails.getGroup())")
    @Mapping(target = "type", expression = "java(bondDetails.getType())")
    @Mapping(target = "groupName", expression = "java(bondDetails.getGroupName())")
    @Mapping(target = "emitterId", expression = "java(bondDetails.getEmitterId())")
    @Mapping(target = "creditRatingOfCompany", expression = "java(extendedBondDetail.getCreditRatingOfCompany())")
    @Mapping(target = "offerDate", expression = "java(CheckAndParseData.parseOffsetDateTime(extendedBondDetail.getOfferDate()))")
    @Mapping(target = "yieldToCallOption", expression = "java(CheckAndParseData.parseBigDecimal(extendedBondDetail.getYieldToCallOption()))")
    @Mapping(target = "typeOfCouponValue", expression = "java(extendedBondDetail.getTypeOfCouponValue())")
    @Mapping(target = "accruedCouponIncome", expression = "java(CheckAndParseData.parseBigDecimal(extendedBondDetail.getAccruedCouponIncome()))")
    @Mapping(target = "subordination", expression = "java(extendedBondDetail.getSubordination())")
    @Mapping(target = "amortization", expression = "java(extendedBondDetail.getAmortization())")
    @Mapping(target = "currentBondPrice", expression = "java(CheckAndParseData.parseBigDecimal(extendedBondDetail.getCurrentBondPrice()))")
    @Mapping(target = "yieldToOfferDate", expression = "java(CheckAndParseData.parseBigDecimal(extendedBondDetail.getYieldToOfferDate()))")
    @Mapping(target = "yieldToMatDate", expression = "java(CheckAndParseData.parseBigDecimal(extendedBondDetail.getYieldToMatDate()))")
    Bond mapToEntity(BondDetails bondDetails, ExtendedBondDetail extendedBondDetail);

}
