package com.myprojects.java_bonds_advisor.utils;

import com.google.gson.JsonArray;
import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.dto.input.BondHistoryInformation;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class MoexDtoCollectorUtils {

    /**
     * The auxiliary method, which collects 'BondHistoryInformation' from got elems (from request answer).
     *
     * @param elem - the unit of information about bond
     * @return - ready essence
     */
    public static BondHistoryInformation createBondByJsonInnerElems(JsonArray elem, Map<String, Integer> fieldIndexMap) {
        return BondHistoryInformation.builder()
                .secId(getFieldAsString(elem, fieldIndexMap, "SECID"))
                .boardId(getFieldAsString(elem, fieldIndexMap, "BOARDID"))
                .tradeDate(getFieldAsString(elem, fieldIndexMap, "TRADEDATE"))
                .shortName(getFieldAsString(elem, fieldIndexMap, "SHORTNAME"))
                .numTrades(getFieldAsString(elem, fieldIndexMap, "NUMTRADES"))
                .value(getFieldAsString(elem, fieldIndexMap, "VALUE"))
                .low(getFieldAsString(elem, fieldIndexMap, "LOW"))
                .high(getFieldAsString(elem, fieldIndexMap, "HIGH"))
                .close(getFieldAsString(elem, fieldIndexMap, "CLOSE"))
                .legalClosePrice(getFieldAsString(elem, fieldIndexMap, "LEGALCLOSEPRICE"))
                .accInt(getFieldAsString(elem, fieldIndexMap, "ACCINT"))
                .waPrice(getFieldAsString(elem, fieldIndexMap, "WAPRICE"))
                .yieldClose(getFieldAsString(elem, fieldIndexMap, "YIELDCLOSE"))
                .open(getFieldAsString(elem, fieldIndexMap, "OPEN"))
                .volume(getFieldAsString(elem, fieldIndexMap, "VOLUME"))
                .marketPrice2(getFieldAsString(elem, fieldIndexMap, "MARKETPRICE2"))
                .marketPrice3(getFieldAsString(elem, fieldIndexMap, "MARKETPRICE3"))
                .admittedQuote(getFieldAsString(elem, fieldIndexMap, "ADMITTEDQUOTE"))
                .mp2ValTrd(getFieldAsString(elem, fieldIndexMap, "MP2VALTRD"))
                .marketPrice3TradesValue(getFieldAsString(elem, fieldIndexMap, "MARKETPRICE3TRADESVALUE"))
                .admittedValue(getFieldAsString(elem, fieldIndexMap, "ADMITTEDVALUE"))
                .matDate(getFieldAsString(elem, fieldIndexMap, "MATDATE"))
                .duration(getFieldAsString(elem, fieldIndexMap, "DURATION"))
                .yieldAtWap(getFieldAsString(elem, fieldIndexMap, "YIELDATWAP"))
                .iriCpiClose(getFieldAsString(elem, fieldIndexMap, "IRICPICLOSE"))
                .beiClose(getFieldAsString(elem, fieldIndexMap, "BEICLOSE"))
                .couponPercent(getFieldAsString(elem, fieldIndexMap, "COUPONPERCENT"))
                .couponValue(getFieldAsString(elem, fieldIndexMap, "COUPONVALUE"))
                .buyBackDate(getFieldAsString(elem, fieldIndexMap, "BUYBACKDATE"))
                .lastTradeDate(getFieldAsString(elem, fieldIndexMap, "LASTTRADEDATE"))
                .faceValue(getFieldAsString(elem, fieldIndexMap, "FACEVALUE"))
                .currentCyId(getFieldAsString(elem, fieldIndexMap, "CURRENCYID"))
                .cbrClose(getFieldAsString(elem, fieldIndexMap, "CBRCLOSE"))
                .yieldToOffer(getFieldAsString(elem, fieldIndexMap, "YIELDTOOFFER"))
                .yieldLastCoupon(getFieldAsString(elem, fieldIndexMap, "YIELDLASTCOUPON"))
                .offerDate(getFieldAsString(elem, fieldIndexMap, "OFFERDATE"))
                .faceUnit(getFieldAsString(elem, fieldIndexMap, "FACEUNIT"))
                .tradingSession(getFieldAsString(elem, fieldIndexMap, "TRADINGSESSION"))
                .build();
    }

    /**
     * The auxiliary method, which helps to avoid NPE
     *
     * @param elem          - array with bond's information
     * @param fieldIndexMap - created map, where key is name of necessary field and value is index of inner JsonArray
     * @param fieldName     - name of necessary field
     * @return - in success: index of inner elem of JsonArray. In fail: null
     */
    private static String getFieldAsString(JsonArray elem, Map<String, Integer> fieldIndexMap, String fieldName) {
        Integer index = fieldIndexMap.get(fieldName);
        if (index != null && index < elem.size() && !elem.get(index).isJsonNull()) {
            return elem.get(index).getAsString();
        }
        return null;
    }

    /**
     * The auxiliary method, which collects 'BondDetails'
     *
     * @param nameAndValue - the unit of information about bond
     * @param boardId      - extra information about where this bond is merchanted
     * @return - ready essence
     */
    public static BondDetails createMOEXBondDetails(Map<String, String> nameAndValue, String boardId) {
        return BondDetails.builder()
                .boardId(boardId)
                .secId(nameAndValue.getOrDefault("SECID", null))
                .name(nameAndValue.getOrDefault("NAME", null))
                .shortName(nameAndValue.getOrDefault("SHORTNAME", null))
                .regNumber(nameAndValue.getOrDefault("REGNUMBER", null))
                .isin(nameAndValue.getOrDefault("ISIN", null))
                .issueDate(nameAndValue.getOrDefault("ISSUEDATE", null))
                .matDate(nameAndValue.getOrDefault("MATDATE", null))
                .initialFaceValue(nameAndValue.getOrDefault("INITIALFACEVALUE", null))
                .faceUnit(nameAndValue.getOrDefault("FACEUNIT", null))
                .latName(nameAndValue.getOrDefault("LATNAME", null))
                .startDateMoex(nameAndValue.getOrDefault("STARTDATEMOEX", null))
                .programRegistryNumber(nameAndValue.getOrDefault("PROGRAMREGISTRYNUMBER", null))
                .listLevel(nameAndValue.getOrDefault("LISTLEVEL", null))
                .daysToRedemption(nameAndValue.getOrDefault("DAYSTOREDEMPTION", null))
                .issueSize(nameAndValue.getOrDefault("ISSUESIZE", null))
                .faceValue(nameAndValue.getOrDefault("FACEVALUE", null))
                .isQualifiedInvestors(nameAndValue.getOrDefault("ISQUALIFIEDINVESTORS", null))
                .couponFrequency(nameAndValue.getOrDefault("COUPONFREQUENCY", null))
                .couponDate(nameAndValue.getOrDefault("COUPONDATE", null))
                .couponPercent(nameAndValue.getOrDefault("COUPONPERCENT", null))
                .couponValue(nameAndValue.getOrDefault("COUPONVALUE", null))
                .typeName(nameAndValue.getOrDefault("TYPENAME", null))
                .group(nameAndValue.getOrDefault("GROUP", null))
                .type(nameAndValue.getOrDefault("TYPE", null))
                .groupName(nameAndValue.getOrDefault("GROUPNAME", null))
                .emitterId(nameAndValue.getOrDefault("EMITTER_ID", null))
                .build();
    }

    /**
     * The auxiliary method, which writes index of each elem from inner JsonElement (JsonArray)
     *
     * @param jsonArray - array of necessary fields
     * @return - a map, where a key is name of field and a value is an index in the initial array
     */
    public static Map<String, Integer> getMapWithIndexOfInnerJsonElem(JsonArray jsonArray) {
        return IntStream.range(0, jsonArray.size()) // Create a stream of indices
                .boxed() // Box indices to Integer for map compatibility
                .collect(Collectors.toMap(
                        i -> jsonArray.get(i).getAsString(), // Key
                        i -> i // Value
                ));
    }
}
